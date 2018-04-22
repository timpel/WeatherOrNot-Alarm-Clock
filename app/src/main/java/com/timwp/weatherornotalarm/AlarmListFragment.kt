package com.timwp.weatherornotalarm

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.os.Bundle
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson


class AlarmListFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(
                R.layout.activity_main, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val alarmList: ListView = view!!.findViewById(R.id.alarmList)
        alarmList.adapter = AlarmListFragment.AlarmListAdapter(context)
        listPersistedAlarms(context)
    }

    fun addAlarm(view: View) {
        val launchIntent = Intent(context, SetAlarmActivity::class.java)
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
            viewHolder.alarmTime.text = util.timeString(viewHolder.thisAlarm.getAlarmTime())
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

    }
}