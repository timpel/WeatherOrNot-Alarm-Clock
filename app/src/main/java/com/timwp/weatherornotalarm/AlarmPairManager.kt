package com.timwp.weatherornotalarm

import android.content.Context
import android.util.Log
import com.google.gson.Gson

class AlarmPairManager private constructor(context: Context) {
    companion object : SingletonHolder<AlarmPairManager, Context>(::AlarmPairManager)
    private var alarmPairMap = mutableMapOf<Int, AlarmPair>()

    fun addAlarmPair(alarmPair: AlarmPair) {
        alarmPairMap[alarmPair.getID()] = alarmPair
    }

    fun removeAlarmPair(id: Int) {
        alarmPairMap.remove(id)
    }

    fun getAlarmPairByID(id: Int): AlarmPair? {
        return alarmPairMap[id]
    }

    fun numberOfAlarmPairs(): Int {
        return alarmPairMap.size
    }

    fun listAlarmPairs(): ArrayList<AlarmPair> {
        return ArrayList(alarmPairMap.toList().map { pair -> pair.second })
    }

    fun update(context: Context) {
        val files =  context.filesDir.listFiles { dir, filename -> filename != "instant-run" }
        if (files.size != numberOfAlarmPairs()) {
            val gson = Gson()
            files.forEach {
                try {
                    val persistedAlarmPair = gson.fromJson(it.readText(), PersistentAlarmPairSettings::class.java)
                    val defaultAlarm = if (persistedAlarmPair.defaultAlarmSettings == null) null
                                                else Alarm(persistedAlarmPair.defaultAlarmSettings, context)
                    val weatherAlarm = if (persistedAlarmPair.weatherAlarmSettings == null) null
                                                else Alarm(persistedAlarmPair.weatherAlarmSettings, context)
                    addAlarmPair(AlarmPair(persistedAlarmPair.id, defaultAlarm, weatherAlarm, persistedAlarmPair.active, context))
                } catch(err: Error) {
                    Log.e("AlarmPairManager", "Could not add alarm pair to AlarmPairManager")
                }
            }
        }
    }
}