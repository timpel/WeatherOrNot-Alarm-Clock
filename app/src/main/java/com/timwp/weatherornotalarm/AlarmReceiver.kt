package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals("com.timwp.alarmtrigger")) {
            Toast.makeText(context,"Ring ring ring", Toast.LENGTH_LONG).show()
        }
    }
}