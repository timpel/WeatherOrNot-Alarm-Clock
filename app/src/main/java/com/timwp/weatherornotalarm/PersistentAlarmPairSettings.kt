package com.timwp.weatherornotalarm

data class PersistentAlarmPairSettings (
        val id: Int,
        val defaultAlarmSettings: IAlarmSettings?,
        val weatherAlarmSettings: IAlarmSettings?,
        val active: Boolean
)