package com.timwp.weatherornotalarm

import android.content.Context

class LocalAlarmManager private constructor(context: Context) {
    companion object : SingletonHolder<LocalAlarmManager, Context>(::LocalAlarmManager)
    private var alarms = mutableListOf<Alarm>()

    fun addAlarm(alarm: Alarm) {
        alarms.add(alarm)
        alarms.sort()
    }

    fun removeAlarm(alarm: Alarm) {
        alarms.remove(alarm)
    }

    fun getAlarmByPosition(idx: Int): Alarm {
        return alarms[idx]
    }

    fun numberOfAlarms(): Int {
        return alarms.size
    }
}