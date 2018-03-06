package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Created by tim on 05/03/18.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals("com.timwp.alarmtrigger")) {
            Toast.makeText(context,"Ring ring ring", Toast.LENGTH_LONG).show()
        }
    }
}