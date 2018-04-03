package com.timwp.weatherornotalarm

import android.content.Context
import java.util.*

data class IAlarmSettings(
    val id: Int,
    val time: Long,
    val location: String,
    val criteria: IWeatherCriteria,
    val keepChecking: String,
    val snoozeTime: String,
    val repeat: Array<String>
)