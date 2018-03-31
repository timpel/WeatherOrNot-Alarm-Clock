package com.timwp.weatherornotalarm

interface IWeatherCriteria {
    val conditions: String
    val tempOperator: String
    val temp: String
    val windOperator: String
    val windSpeed: String
    val windDirection: String
    val fog: String
}