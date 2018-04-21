package com.timwp.weatherornotalarm

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import java.util.*
import java.util.Calendar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val alarmList: ListView = findViewById(R.id.alarmList)
        alarmList.adapter = AlarmListAdapter(this)
        listPersistedAlarms(this)
    }

    fun addAlarm(view: View) {
        val launchIntent = Intent(applicationContext, SetAlarmActivity::class.java)
        startActivity(launchIntent)
        Log.e("Logging", "in addAlarm")
    }

    fun listPersistedAlarms(context: Context) {
        val files =  context.filesDir.listFiles { dir, filename -> filename != "instant-run" }
        val localAlarmManager = LocalAlarmManager.getInstance(context)
        if (files.size != localAlarmManager.numberOfAlarms()) {
            val gson = Gson()
            files.forEach {
                try {
                    val persistedAlarm = gson.fromJson(it.readText(), PersistentAlarmSettings::class.java)
                    val alarm = Alarm(persistedAlarm.settings, context)
                    if (!persistedAlarm.active) alarm.deactivate()
                    localAlarmManager.addAlarm(alarm)
                    Log.i("MainActivity", "Alarm " + alarm.getID() + " added to localAlarmManager")
                } catch(err: Error) {
                    Log.e("MainActivity", "Could not add file to localAlarmManager")
                }
            }
        }
    }

    private class AlarmListAdapter(val mContext: Context): BaseAdapter() {

        private val localAlarmManager = LocalAlarmManager.getInstance(mContext)

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val rowMain: View
            val layoutInflater : LayoutInflater = LayoutInflater.from(mContext)
            rowMain = layoutInflater.inflate(R.layout.alarm_item, viewGroup, false)
            val thisAlarm: Alarm = localAlarmManager.getAlarmByPosition(position)
            val alarmTime = rowMain.findViewById<TextView>(R.id.alarm_time)
            val cancelButton = rowMain.findViewById<AppCompatImageButton>(R.id.cancel_button)
            val viewHolder = ViewHolder(thisAlarm, alarmTime, cancelButton)
            rowMain.tag = viewHolder
            viewHolder.alarmTime.text = alarmString(viewHolder.thisAlarm.getAlarmTime())
            viewHolder.cancelButton.setOnClickListener(object: View.OnClickListener {
                override fun onClick(view: View) {
                    viewHolder.thisAlarm.cancel()
                    localAlarmManager.removeAlarm(viewHolder.thisAlarm)
                    this@AlarmListAdapter.notifyDataSetInvalidated()
                }
            })
            return rowMain
        }

        private class ViewHolder(val thisAlarm: Alarm, val alarmTime: TextView,
                                 val cancelButton: AppCompatImageButton)

        override fun getItem(position: Int): Any {
            return localAlarmManager.getAlarmByPosition(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return localAlarmManager.numberOfAlarms()
        }

        fun alarmString(time: Long): String {
            val cal = Calendar.getInstance()
            cal.setTimeInMillis(time)
            val hour = cal.get(HOUR)
            val hourString = if (hour == 0) "12" else "$hour"
            val minute = cal.get(MINUTE)
            val minuteString = if (minute < 10) "0$minute" else "$minute"
            val am_pm = if (cal.get(AM_PM) == 1) "pm" else "am"
            return "$hourString:$minuteString $am_pm"
        }

    }
}
