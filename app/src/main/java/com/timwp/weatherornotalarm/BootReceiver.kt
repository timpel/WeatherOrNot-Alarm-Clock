package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import java.util.*

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
        val files =  context.filesDir.listFiles { dir, filename -> filename != "instant-run" }
        if (files.isNotEmpty()) {
            val gson = Gson()
            files.forEach {
                try {
                    var persistedAlarm = gson.fromJson(it.readText(), PersistentAlarmSettings::class.java)
                    persistedAlarm.settings.time = updateAlarmTime(persistedAlarm.settings.time,
                            persistedAlarm.settings.hour,
                            persistedAlarm.settings.minute)
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
    fun updateAlarmTime(oldTime: Long, hour: Int, minute: Int): Long {
        Log.i("updateAlarmTime", "Old time: " + oldTime)
        val cal = Calendar.getInstance()
        val currentTime = cal.timeInMillis
        Log.i("updateAlarmTime", "Current: " + currentTime)
        return if (oldTime > currentTime) {
            Log.i("updateAlarmTime", "Old time still in future, not changing")
            oldTime
        }
        else {
            Log.i("updateAlarmTime", "Old time in the past, resetting for today")
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            var newTime = cal.timeInMillis
            Log.i("updateAlarmTime", "new time (naive): " + newTime)
            if (newTime < currentTime) {
                cal.add(Calendar.HOUR_OF_DAY, 24)
            } else {
                Log.i("updateAlarmTime", "new time is in future, we hope")
            }
            newTime = cal.timeInMillis
            Log.i("updateAlarmTime", "New time: " + newTime)
            newTime

        }
    }
}