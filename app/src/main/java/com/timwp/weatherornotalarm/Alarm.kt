package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.math.abs

class Alarm(private val settings: IAlarmSettings, con: Context): Comparable<Alarm> {
    companion object {
        val ALARM_TYPE_DEFAULT = 0
        val ALARM_TYPE_WEATHER = 1
    }

    override fun compareTo(other: Alarm): Int {
        return (if (alarmTime > other.getAlarmTime()) 1 else -1)
    }

    private val SNOOZE_TIME = 10000 //* 60 * 10

    private val localAlarmManager = LocalAlarmManager.getInstance(con)
    private var context = con
    private var alarmTime = settings.time
    private var loc = settings.location
    private var ringing = false
    private var checkAgain = settings.keepChecking === "true"
    private val alarmID = settings.id
    private val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, AlarmReceiver::class.java)
    lateinit var pendingAlarmIntent: PendingIntent
    private var active = true
    private val ringtoneURI = Uri.parse(settings.ringtoneURIString)
    lateinit var ringtone: Ringtone
    private var repeat: BooleanArray = settings.repeat

    fun getSettings(): IAlarmSettings {
        return settings
    }

    fun getID(): Int {
        return alarmID
    }

    fun getAlarmTime(): Long {
        return alarmTime
    }

    fun getWeatherCriteria(): IWeatherCriteria {
        return settings.criteria
    }

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
        pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingAlarmIntent)

        val testCalendar = Calendar.getInstance()
        testCalendar.timeInMillis = alarmTime
        Log.e("Alarm set()", "Alarm set for " + testCalendar.get(Calendar.HOUR_OF_DAY) + ":" + testCalendar.get(Calendar.MINUTE)
                + ", " + testCalendar.get(Calendar.DAY_OF_YEAR))
        //localAlarmManager.addAlarm(this)
        //persist()
    }
    fun edit(settings: IAlarmSettings, con: Context) {
        cancel()
        alarmTime = settings.time
        loc = settings.location
        checkAgain = settings.keepChecking === "true"
        set()
    }
    fun cancel() {
        alarmIntent.action = "com.timwp.alarmtrigger"
        pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.cancel(pendingAlarmIntent)
        //depersist()
    }
    fun trigger() {
        if (matchesWeatherCriteria()) ring()
        else if (checkAgain) snooze()
        else cancel()
    }
    fun snooze() {
        ringing = false
        alarmIntent.action = "com.timwp.alarmtrigger"
        alarmIntent.putExtra("ALARM_TYPE", settings.type)
        alarmIntent.putExtra("ALARM_PAIR_ID", settings.pairID)
        pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, addSnoozeTime(), pendingAlarmIntent)
    }
    fun stop() {
        ringing = false
        ringtone.stop()
    }
    fun ring() {
        ringtone = RingtoneManager.getRingtone(context, ringtoneURI)
        ringtone.play()
    }
    fun setForTomorrow() {
        val oldTime = Calendar.getInstance()
        oldTime.timeInMillis = settings.time
        Log.e("setForTomorrow", "Old alarm time: " + oldTime.get(Calendar.HOUR_OF_DAY) + ":" + oldTime.get(Calendar.MINUTE)
                + ", " + oldTime.get(Calendar.DAY_OF_YEAR))
        val newAlarmTime = util.setCalendar(util.Companion.TimeObject(settings.hour, settings.minute))
        settings.time = newAlarmTime.timeInMillis
        val testTime = Calendar.getInstance()
        testTime.timeInMillis = settings.time
        Log.e("setForTomorrow", "New alarm time: " + testTime.get(Calendar.HOUR_OF_DAY) + ":" + testTime.get(Calendar.MINUTE)
            + ", " + testTime.get(Calendar.DAY_OF_YEAR))
    }
    fun matchesWeatherCriteria(): Boolean {
        return false
    }
    fun addSnoozeTime(): Long {
        return Calendar.getInstance().timeInMillis + SNOOZE_TIME
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