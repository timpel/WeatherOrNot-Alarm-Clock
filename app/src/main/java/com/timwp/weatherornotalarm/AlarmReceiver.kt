package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.google.gson.Gson
import java.io.File

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent!!.action.equals("com.timwp.alarmtrigger")) {

            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            val localAlarmManager = LocalAlarmManager.getInstance(context)
            localAlarmManager.update(context)

            val thisAlarm = localAlarmManager.getAlarmByID(alarmId) ?: return

            if (thisAlarm.isActive()) {
                Log.i("AlarmReceiver", "Alarm " + alarmId + " should ring")
                val launchIntent = Intent(context, RingSliderActivity::class.java)
                launchIntent.putExtra("ALARM_ID", alarmId)
                launchIntent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                startActivity(context, launchIntent, null)
            }

        }
    }
}