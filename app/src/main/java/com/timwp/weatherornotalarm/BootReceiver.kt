package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (context != null) resetAlarms(context)
            else {
                Toast.makeText(context, "context null", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun resetAlarms(context: Context) {
        println("in resetAlarms")
        val files =  context.filesDir.listFiles { dir, filename -> filename != "instant-run" }
        if (files.isNotEmpty()) {
            val gson = Gson()
            files.forEach {
                try {
                    val persistedAlarm = gson.fromJson(it.readText(), PersistentAlarmSettings::class.java)
                    val alarm = Alarm(persistedAlarm.settings, context)
                    if (!persistedAlarm.active) alarm.deactivate()
                    alarm.set()
                    Log.i("BootReceiver", "Alarm " + alarm.getID() + " set")
                } catch(err: Error) {
                    Log.e("BootReceiver", "Could not read file as alarm")
                }
            }
        }
    }
}