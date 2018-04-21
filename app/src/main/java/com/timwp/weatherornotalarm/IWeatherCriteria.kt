package com.timwp.weatherornotalarm

data class IWeatherCriteria(
    var conditions: String,
    var tempOperator: String,
    var temp: String,
    var windOperator: String,
    var windSpeed: String,
    var windDirection: String,
    var fog: String
)