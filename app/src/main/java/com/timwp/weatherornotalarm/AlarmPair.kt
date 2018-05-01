package com.timwp.weatherornotalarm

import android.content.Context
import com.google.gson.GsonBuilder
import java.io.File

class AlarmPair(private val id: Int, private val defaultAlarm: Alarm?, private val weatherAlarm: Alarm?,
                private var active: Boolean, private val context: Context) {
    fun getDefaultAlarm() = defaultAlarm
    fun getWeatherAlarm() = weatherAlarm
    fun getID() = id
    fun isActive() = active

    fun getAlarmByType(type: Int): Alarm? {
        return if (type == Alarm.ALARM_TYPE_DEFAULT) defaultAlarm else weatherAlarm
    }

    fun activate() {
        active = true
        persist()
    }

    fun deactivate() {
        active = false
        persist()
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