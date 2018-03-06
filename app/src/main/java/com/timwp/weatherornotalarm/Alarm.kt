package com.timwp.weatherornotalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import java.util.*

/**
 * Created by tim on 04/03/18.
 */
class Alarm(con: Context, time: Date, location: String, criteria: WeatherCriteria, keepChecking: Boolean, snoozeTime: Int) {
    private var context = con
    private var alarmTime = time
    private var loc = location
    private var ringing = false
    private var checkAgain = keepChecking
    private var snooze = snoozeTime

    fun set() {
        val TEST_ALARM_TIME = Calendar.getInstance().timeInMillis + 15000
        val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.action = "com.timwp.alarmtrigger"
        val pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        systemAlarmManager.setExact(AlarmManager.RTC_WAKEUP, TEST_ALARM_TIME, pendingAlarmIntent)
    }
    fun edit(context: Context, time: Date, location: String, criteria: WeatherCriteria, keepChecking: Boolean, snoozeTime: Int) {
        cancel()
        alarmTime = time
        loc = location
        checkAgain = keepChecking
        snooze = snoozeTime
        set()
    }
    fun cancel() {

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
    fun addSnoozeTime(): Date {
        return alarmTime
    }
}