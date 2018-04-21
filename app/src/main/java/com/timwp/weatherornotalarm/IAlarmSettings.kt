package com.timwp.weatherornotalarm

import android.content.Context
import java.util.*

data class IAlarmSettings(
    var id: Int,
    var time: Long,
    var hour: Int,
    var minute: Int,
    var location: String,
    var criteria: IWeatherCriteria,
    var keepChecking: String,
    var snoozeTime: String,
    var repeat: Array<String>
)