package com.timwp.weatherornotalarm

data class IWeatherCriteria(
    var conditions: String,
    var tempOperator: String,
    var temp: String,
    var tempUnit: String,
    var windOperator: String,
    var windSpeed: String,
    var windUnit: String,
    var windDirection: String
)