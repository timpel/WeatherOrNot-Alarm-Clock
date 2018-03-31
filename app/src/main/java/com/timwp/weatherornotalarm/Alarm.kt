package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import java.util.*

class Alarm(settings: IAlarmSettings, con: Context): Comparable<Alarm> {
    override fun compareTo(other: Alarm): Int {
        return (if (alarmTime > other.getAlarmTime()) 1 else -1)
    }

    private val localAlarmManager = LocalAlarmManager.getInstance(con)
    private var context = con
    private var alarmTime = settings.time
    private var loc = settings.location
    private var ringing = false
    private var checkAgain = settings.keepChecking === "true"
    private var snooze = settings.snoozeTime
    private val alarmID = (Calendar.getInstance().timeInMillis).toInt()
    private val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmIntent = Intent(context, AlarmReceiver::class.java)
    lateinit var pendingAlarmIntent: PendingIntent

    fun getID(): Int {
        return alarmID
    }

    fun getAlarmTime(): Long {
        return alarmTime
    }

    fun set() {
        alarmIntent.action = "com.timwp.alarmtrigger"
        pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingAlarmIntent)
        localAlarmManager.addAlarm(this)
    }
    fun edit(settings: IAlarmSettings, con: Context) {
        cancel()
        alarmTime = settings.time
        loc = settings.location
        checkAgain = settings.keepChecking === "true"
        snooze = settings.snoozeTime
        set()
    }
    fun cancel() {
        systemAlarmManager.cancel(pendingAlarmIntent)
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
    }
    fun ring() {
        cancel()
        // start ringtone
        // make ringing view appear
    }
    fun matchesWeatherCriteria(): Boolean {
        return false
    }
    fun addSnoozeTime(): Long {
        return alarmTime + (if (snooze === "Off") 0 else (snooze.get(0).toInt() * 60000))
    }
}