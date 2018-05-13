package com.timwp.weatherornotalarm

data class IAlarmSettings(
    var type: Int,
    var id: Int,
    var pairID: Int,
    var time: Long,
    var hour: Int,
    var minute: Int,
    var location: String,
    var criteria: IWeatherCriteria,
    var keepChecking: String,
    var repeat: BooleanArray,
    var ringtoneURIString: String
)