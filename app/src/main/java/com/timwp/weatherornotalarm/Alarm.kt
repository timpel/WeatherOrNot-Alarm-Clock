package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import java.util.*
import kotlin.math.abs

class Alarm(private val settings: IAlarmSettings, con: Context): Comparable<Alarm> {
    companion object {
        val ALARM_TYPE_DEFAULT = 0
        val ALARM_TYPE_WEATHER = 1
    }

    override fun compareTo(other: Alarm): Int {
        return (if (settings.time > other.getAlarmTime()) 1 else -1)
    }

    private val SNOOZE_TIME = 10000 //* 60 * 10
    private var context = con
    private var ringing = false
    private val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, AlarmReceiver::class.java)
    lateinit var pendingAlarmIntent: PendingIntent
    private var active = true
    private val ringtoneURI = Uri.parse(settings.ringtoneURIString)
    lateinit var ringtone: Ringtone
    private var isSnoozing = false

    fun getSettings(): IAlarmSettings {
        return settings
    }

    fun getID(): Int {
        return settings.id
    }

    fun getAlarmTime(): Long {
        return settings.time
    }

    fun getWeatherCriteria(): IWeatherCriteria {
        return settings.criteria
    }

    fun getType(): Int {
        return settings.type
    }

    fun isSnoozing() = isSnoozing

    fun getWeatherCriteriaAsStringArray(): Array<String> {
        val criteria = settings.criteria
        return arrayOf(
                criteria.conditions,
                criteria.tempOperator,
                criteria.temp,
                criteria.tempUnit,
                criteria.windOperator,
                criteria.windSpeed,
                criteria.windUnit,
                criteria.windDirection)
    }
/*
    fun activate() {
        active = true
        persist()
    }

    fun deactivate() {
        active = false
        persist()
    }
*/
    fun isActive(): Boolean {
        return active
    }

    fun set() {
        alarmIntent.action = "com.timwp.alarmtrigger"
        alarmIntent.putExtra("ALARM_TYPE", settings.type)
        alarmIntent.putExtra("ALARM_PAIR_ID", settings.pairID)
        pendingAlarmIntent = PendingIntent.getBroadcast(context, settings.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, settings.time, pendingAlarmIntent)

        val testCalendar = Calendar.getInstance()
        testCalendar.timeInMillis = settings.time
        Log.e("Alarm set()", "Alarm set for " + testCalendar.get(Calendar.HOUR_OF_DAY) + ":" + testCalendar.get(Calendar.MINUTE)
                + ", " + testCalendar.get(Calendar.DAY_OF_YEAR))
        //localAlarmManager.addAlarm(this)
        //persist()
    }
    /*
    fun edit(newSettings: IAlarmSettings, con: Context) {
        cancel()
        this.settings.time = settings.time
        loc = settings.location
        checkAgain = settings.keepChecking === "true"
        set()
    }
    */
    fun cancel() {
        alarmIntent.action = "com.timwp.alarmtrigger"
        pendingAlarmIntent = PendingIntent.getBroadcast(context, settings.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.cancel(pendingAlarmIntent)
        //depersist()
    }
    /*
    fun trigger() {
        if (matchesWeatherCriteria()) ring()
        else if (settings.checkAgain) snooze()
        else cancel()
    }
    */
    fun snooze() {
        ringing = false
        alarmIntent.action = "com.timwp.alarmtrigger"
        alarmIntent.putExtra("ALARM_TYPE", settings.type)
        alarmIntent.putExtra("ALARM_PAIR_ID", settings.pairID)
        pendingAlarmIntent = PendingIntent.getBroadcast(context, settings.id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, addSnoozeTime(), pendingAlarmIntent)
        isSnoozing = true
    }
    fun cancelSnooze() {
        isSnoozing = false
    }
    fun stop() {
        ringing = false
        ringtone.stop()
    }
    fun ring() {
        cancelSnooze()
        ringtone = RingtoneManager.getRingtone(context, ringtoneURI)
        ringtone.play()
    }
    fun setForTomorrow() {
        val type = if (getType() == ALARM_TYPE_DEFAULT) "Default" else "Weather"
        val oldTime = Calendar.getInstance()
        oldTime.timeInMillis = settings.time
        Log.e("setForTomorrow", "Old " + type + " alarm time: " + oldTime.get(Calendar.HOUR_OF_DAY) + ":" + oldTime.get(Calendar.MINUTE)
                + ", " + oldTime.get(Calendar.DAY_OF_YEAR))
        val newAlarmTime = Calendar.getInstance()
        newAlarmTime.set(Calendar.HOUR_OF_DAY, settings.hour)
        newAlarmTime.set(Calendar.MINUTE, settings.minute)
        newAlarmTime.set(Calendar.SECOND, 0)
        newAlarmTime.add(Calendar.DAY_OF_YEAR, 1)
        settings.time = newAlarmTime.timeInMillis
        val testTime = Calendar.getInstance()
        testTime.timeInMillis = settings.time
        Log.e("setForTomorrow", "New " + type + " alarm time: " + testTime.get(Calendar.HOUR_OF_DAY) + ":" + testTime.get(Calendar.MINUTE)
            + ", " + testTime.get(Calendar.DAY_OF_YEAR))
    }

    private fun addSnoozeTime(): Long {
        return Calendar.getInstance().timeInMillis + SNOOZE_TIME
    }
    fun addADay() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = settings.time
        Log.e("Alarm addADay()", "Old time: " + calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        settings.time = calendar.timeInMillis
        Log.e("Alarm addADay()", "Old time: " + calendar.time)
    }

    fun matchesCriteria(weatherResponse: WeatherResponse): Boolean {
        val currentWeather = weatherResponse.currently

        val clear = (currentWeather.cloudCover != null && currentWeather.cloudCover < 0.2)
                || currentWeather.icon == "clear-day"
                || currentWeather.icon == "clear-night"
        val cloudy = (currentWeather.cloudCover != null && currentWeather.cloudCover > 0.6)
                || currentWeather.icon == "cloudy"
        val raining = currentWeather.precipType == "rain"
                || currentWeather.precipType == "sleet"
                || currentWeather.icon == "rain"
        val snowing = currentWeather.precipType == "snow"
                || currentWeather.precipType == "sleet"
                || currentWeather.icon == "snow"
        val tempInFahrenheit = currentWeather.temperature
        val windSpeedInMph = currentWeather.windSpeed
        val windBearing = currentWeather.windBearing

        val criteria = settings.criteria
        return when {
            criteria.conditions == context.getString(R.string.clear) -> {
                clear
            }
            criteria.conditions == context.getString(R.string.rain) -> {
                raining
            }
            criteria.conditions == context.getString(R.string.snow) -> {
                snowing
            }
            criteria.conditions == context.getString(R.string.cloud) -> {
                cloudy
            }
            criteria.temp != "" -> {
                val criteriaTempInFahrenheit = if (criteria.tempUnit == "F") criteria.temp.toFloat()
                else util.celsiusToFahrenheit(criteria.temp.toInt())
                tempInFahrenheit == null ||
                when (criteria.tempOperator) {
                    context.getString(R.string.temp_above) -> {
                        tempInFahrenheit >= criteriaTempInFahrenheit
                    }
                    context.getString(R.string.temp_below) -> {
                        tempInFahrenheit <= criteriaTempInFahrenheit
                    }
                    else -> {
                        Log.e("matchesCriteria", "Temp operator not recognized")
                        true
                    }
                }
            }
            criteria.windSpeed != "" -> {
                val criteriaWindSpeedInMph = if (criteria.windUnit == context.getString(R.string.mph)) criteria.windSpeed.toFloat()
                else util.kmToMph(criteria.windSpeed.toInt())
                when (criteria.windOperator) {
                    context.getString(R.string.wind_above) -> {
                        windSpeedInMph != null
                        && windSpeedInMph >= criteriaWindSpeedInMph
                        && windDirectionMatch(windBearing, criteria.windDirection)
                    }
                    context.getString(R.string.wind_below) -> {
                        windSpeedInMph == null
                        || windSpeedInMph <= criteriaWindSpeedInMph
                        || !windDirectionMatch(windBearing, criteria.windDirection)
                    }
                    else -> {
                        Log.e("matchesCriteria", "Wind operator not recognized")
                        true
                    }
                }
            }
            else -> {
                Log.e("matchesCriteria", "Criteria check error")
                true
            }
        }

    }
    private fun windDirectionMatch(currentWindBearing: Float?, criteriaWindDirection: String): Boolean {
        return if (criteriaWindDirection == "" || currentWindBearing == null) true
        else {
            val criteriaBearing = toBearing(criteriaWindDirection)
            (abs(criteriaBearing - currentWindBearing) < 45
                    || criteriaWindDirection == "N" && criteriaBearing > 360 * 7/8)
        }
    }
    private fun toBearing(direction: String): Float {
        val dirArray = arrayOf("N","NE","E","SE","S","SW","W","NW")
        val directionIndex = dirArray.indexOf(direction)
        return directionIndex.toFloat() * 360/8
    }
/*
    private fun persist() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val fileObject = PersistentAlarmSettings(settings, active)
        val fileString: String = gson.toJson(fileObject, PersistentAlarmSettings::class.java)

        val path = context.filesDir
        File(path, settings.id.toString()).writeText(fileString)
    }

    private fun depersist() {
        context.deleteFile(settings.id.toString())
    }
    */
}