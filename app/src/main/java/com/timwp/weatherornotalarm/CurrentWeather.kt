package com.timwp.weatherornotalarm

data class CurrentWeather(
        val time: Long?,
        //val summary: String,
        val icon: String?,
        //val nearestStormDistance: Float,
        //val nearestStormBearing: Float,
        val precipIntensity: Float?,
        //val precipProbability: Float,
        val temperature: Float?,
        //val apparentTemperature: Float,
        //val dewPoint: Float,
        //val humidity: Float,
        //val pressure: Float,
        val windSpeed: Float?,
        //val windGust: Float,
        val windBearing: Float?,
        val cloudCover: Float?,
        //val uvIndex: Float,
        //val visibility: Float,
        //val ozone: Float,
        val precipType: String?
)