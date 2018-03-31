package com.timwp.weatherornotalarm

import android.content.Context
import java.util.*

interface IAlarmSettings {
    val time: Long
    val location: String
    val criteria: IWeatherCriteria
    val keepChecking: String
    val snoozeTime: String
    val repeat: Array<String>
}