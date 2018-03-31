package com.timwp.weatherornotalarm

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import java.util.*
import java.util.Calendar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val alarmList: ListView = findViewById(R.id.alarmList)
        alarmList.adapter = AlarmListAdapter(this)
    }

    fun addAlarm(view: View) {
        val launchIntent = Intent(applicationContext, SetAlarmActivity::class.java)
        startActivity(launchIntent)
        Log.e("Logging", "in addAlarm")
    }

    private class AlarmListAdapter(context: Context): BaseAdapter() {

        private val mContext: Context = context
        private val localAlarmManager = LocalAlarmManager.getInstance(mContext)

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val thisAlarm: Alarm = localAlarmManager.getAlarmByPosition(position)
            val layoutInflater : LayoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.alarm_item, viewGroup, false)
            val positionAlarmTime = rowMain.findViewById<TextView>(R.id.alarm_time)
            positionAlarmTime.text = alarmString(thisAlarm.getAlarmTime())
            return rowMain
        }

        override fun getItem(p0: Int): Any {
            return "placeholder"
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return localAlarmManager.numberOfAlarms()
        }

        fun alarmString(time: Long): String {
            val cal = Calendar.getInstance()
            cal.setTimeInMillis(time)
            val hour = if (cal.get(HOUR) == 0) 12 else cal.get(HOUR)
            val minute = cal.get(MINUTE)
            val am_pm = if (cal.get(AM_PM) == 1) "pm" else "am"
            return "$hour:$minute $am_pm"
        }

    }
}
