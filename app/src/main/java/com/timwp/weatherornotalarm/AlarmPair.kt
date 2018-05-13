package com.timwp.weatherornotalarm

import android.content.Context
import com.google.gson.GsonBuilder
import java.io.File
import java.util.*

class AlarmPair(private val id: Int, private val defaultAlarm: Alarm?, private val weatherAlarm: Alarm?,
                private var active: Boolean, private val context: Context) {
    fun getDefaultAlarm() = defaultAlarm
    fun getWeatherAlarm() = weatherAlarm
    fun getID() = id
    fun isActive() = active

    fun getAlarmByType(type: Int): Alarm? {
        return if (type == Alarm.ALARM_TYPE_DEFAULT) defaultAlarm else weatherAlarm
    }

    fun getNonNullAlarm(): Alarm {
        return defaultAlarm ?: weatherAlarm!!
    }

    fun hasDefaultAlarm(): Boolean {
        return defaultAlarm != null
    }

    fun hasWeatherAlarm(): Boolean {
        return weatherAlarm != null
    }

    fun activate() {
        active = true
        persist()
    }

    fun deactivate() {
        active = false
        persist()
    }

    fun cancel() {
        defaultAlarm?.cancel()
        weatherAlarm?.cancel()
        depersist()
    }

    fun cancelSnooze() {
        defaultAlarm?.cancelSnooze()
        weatherAlarm?.cancelSnooze()
    }

    fun moveDefaultToTomorrow() {
        defaultAlarm?.cancel()
        defaultAlarm?.setForTomorrow()
    }

    fun moveWeatherToTomorrow() {
        weatherAlarm?.cancel()
        weatherAlarm?.setForTomorrow()
    }

    fun isSetForToday(): Boolean {
        val repeatDays = getNonNullAlarm().getSettings().repeat
        val daysOfTheWeek = arrayOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
                Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return repeatDays[daysOfTheWeek.indexOf(dayOfWeek)] || isNonRepeating()
    }

    fun isNonRepeating(): Boolean {
        return !getNonNullAlarm().getSettings().repeat.contains(true)
    }

    fun persist() {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val fileObject = PersistentAlarmPairSettings(id, defaultAlarm?.getSettings(),
                weatherAlarm?.getSettings(), active)
        val fileString: String = gson.toJson(fileObject, PersistentAlarmPairSettings::class.java)

        val path = context.filesDir
        File(path, id.toString()).writeText(fileString)
    }

    fun depersist() {
        context.deleteFile(id.toString())
    }
}