package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.google.gson.Gson
import java.io.File

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (context != null && intent.action == "com.timwp.alarmtrigger") {

            val alarmPairId = intent.getIntExtra("ALARM_PAIR_ID", -1)
            val alarmType = intent.getIntExtra("ALARM_TYPE", -1)

            val alarmPairManager = AlarmPairManager.getInstance(context)
            alarmPairManager.update(context)

            //val localAlarmManager = LocalAlarmManager.getInstance(context)
            //localAlarmManager.update(context)

            val thisAlarm = alarmPairManager.getAlarmPairByID(alarmPairId)?.getAlarmByType(alarmType) ?: return

            if (thisAlarm.isActive()) {
                Log.i("AlarmReceiver", "Alarm in " + alarmPairId + " should ring")
                val launchIntent = Intent(context, RingSliderActivity::class.java)
                launchIntent.putExtra("ALARM_PAIR_ID", alarmPairId)
                launchIntent.putExtra("ALARM_TYPE", alarmType)
                launchIntent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                startActivity(context, launchIntent, null)
            }

        }
    }
}