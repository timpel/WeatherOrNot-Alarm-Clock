package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals("com.timwp.alarmtrigger")) {
            val launchIntent = Intent(context, RingSliderActivity::class.java)
            startActivity(context, launchIntent, null)
        }
    }
}