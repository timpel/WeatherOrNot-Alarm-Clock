package com.timwp.weatherornotalarm

import android.app.PendingIntent

data class PersistentAlarmSettings (
    val settings: IAlarmSettings,
    val active: Boolean
    //val pendingIntent: PendingIntent
)