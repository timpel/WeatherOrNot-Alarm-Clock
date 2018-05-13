package com.timwp.weatherornotalarm

data class WeatherResponse(
        val latitude: Float,
        val longitude: Float,
        val currently: CurrentWeather
)