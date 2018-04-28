package com.timwp.weatherornotalarm

import android.content.Context
import android.media.Ringtone
import android.net.Uri
import java.util.*

data class IAlarmSettings(
    var id: Int,
    var time: Long,
    var hour: Int,
    var minute: Int,
    var location: String,
    var criteria: IWeatherCriteria,
    var keepChecking: String,
    var repeat: BooleanArray,
    var ringtoneURIString: String
)