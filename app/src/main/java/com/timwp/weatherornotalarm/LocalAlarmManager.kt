package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.content.Context

class LocalAlarmManager private constructor(context: Context) {
    lateinit var systemAlarmManager: AlarmManager
    init {
        systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    companion object : SingletonHolder<LocalAlarmManager, Context>(::LocalAlarmManager)
}