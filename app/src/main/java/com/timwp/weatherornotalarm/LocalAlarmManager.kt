package com.timwp.weatherornotalarm

import android.content.Context
import android.util.Log
import com.google.gson.Gson

class LocalAlarmManager private constructor(context: Context) {
    companion object : SingletonHolder<LocalAlarmManager, Context>(::LocalAlarmManager)
    private var alarms = mutableListOf<Alarm>()

    fun addAlarm(alarm: Alarm) {
        alarms.add(alarm)
        alarms.sort()
        Log.i("addAlarm", "Alarm added, now " + numberOfAlarms() + " alarms")
    }

    fun removeAlarm(alarm: Alarm) {
        alarms.remove(alarm)
    }

    fun getAlarmByPosition(idx: Int): Alarm {
        return alarms[idx]
    }

    fun getAlarmByID(id: Int): Alarm? {
        return alarms.find { it.getID() == id }
    }

    fun numberOfAlarms(): Int {
        return alarms.size
    }
/*
    fun update(context: Context) {
        val files =  context.filesDir.listFiles { dir, filename -> filename != "instant-run" }
        if (files.size != numberOfAlarms()) {
            val gson = Gson()
            files.forEach {
                try {
                    val persistedAlarm = gson.fromJson(it.readText(), PersistentAlarmSettings::class.java)
                    val alarm = Alarm(persistedAlarm.settings, context)
                    if (!persistedAlarm.active) alarm.deactivate()
                    addAlarm(alarm)
                    Log.i("MainActivity", "Alarm " + alarm.getID() + " added to localAlarmManager")
                } catch(err: Error) {
                    Log.e("MainActivity", "Could not add file to localAlarmManager")
                }
            }
        }
    }
    */
}