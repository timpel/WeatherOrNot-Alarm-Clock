package com.timwp.weatherornotalarm

/**
 * Created by tim on 04/03/18.
 */
data class WeatherCriteria(
    val conditions: String,
    val temp: Number,
    val windSpeed: Number,
    val windDirection: Number,
    val fog: Boolean)