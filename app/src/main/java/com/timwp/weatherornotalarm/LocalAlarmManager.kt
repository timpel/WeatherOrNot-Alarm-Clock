package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.content.Context

/**
 * Created by tim on 04/03/18.
 */
class LocalAlarmManager private constructor(context: Context) {
    lateinit var systemAlarmManager: AlarmManager
    init {
        systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    companion object : SingletonHolder<LocalAlarmManager, Context>(::LocalAlarmManager)
}