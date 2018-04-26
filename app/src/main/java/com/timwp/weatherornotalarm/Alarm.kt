package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import kotlin.math.abs

class Alarm(private val settings: IAlarmSettings, con: Context): Comparable<Alarm> {
    override fun compareTo(other: Alarm): Int {
        return (if (alarmTime > other.getAlarmTime()) 1 else -1)
    }

    private val SNOOZE_TIME = 1000 * 60 * 10

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
    private var repeat: Array<String> = settings.repeat

    fun getID(): Int {
        return alarmID
    }

    fun getAlarmTime(): Long {
        return alarmTime
    }

    fun activate() {
        active = true
        persist()
    }

    fun deactivate() {
        active = false
        persist()
    }

    fun isActive(): Boolean {
        return active
    }

    fun set() {
        alarmIntent.action = "com.timwp.alarmtrigger"
        alarmIntent.putExtra("ALARM_ID", alarmID)
        pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingAlarmIntent)
        localAlarmManager.addAlarm(this)
        persist()
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
        depersist()
    }
    fun trigger() {
        if (matchesWeatherCriteria()) ring()
        else if (checkAgain) snooze()
        else cancel()
    }
    fun snooze() {
        ringing = false
        alarmTime = addSnoozeTime()
        set()
    }
    fun stop() {
        ringing = false
        ringtone.stop()
    }
    fun ring() {
        ringtone = RingtoneManager.getRingtone(context, ringtoneURI)
        ringtone.play()
    }
    fun matchesWeatherCriteria(): Boolean {
        return false
    }
    fun addSnoozeTime(): Long {
        return alarmTime + SNOOZE_TIME
    }

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
}