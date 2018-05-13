package com.timwp.weatherornotalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if (context != null && intent.action == "com.timwp.alarmtrigger") {

            val alarmPairId = intent.getIntExtra("ALARM_PAIR_ID", -1)
            val alarmType = intent.getIntExtra("ALARM_TYPE", -1)

            val alarmPairManager = AlarmPairManager.getInstance(context)
            alarmPairManager.update(context)

            val thisAlarmPair = alarmPairManager.getAlarmPairByID(alarmPairId)
            val thisAlarm = alarmPairManager.getAlarmPairByID(alarmPairId)?.getAlarmByType(alarmType) ?: return

            if (thisAlarm.isSnoozing()) {
                ringAlarm(context, alarmPairId, thisAlarm.getType())
                return
            }

            if (thisAlarmPair == null) {
                Log.e("AlarmReceiver", "Alarm Pair null")
                return
            }
            if (thisAlarmPair.isActive() && thisAlarmPair.isSetForToday()) {
                if (thisAlarm.getType() == Alarm.ALARM_TYPE_DEFAULT) {
                    if (thisAlarmPair.isNonRepeating()) thisAlarmPair.deactivate()
                    ringAlarm(context, alarmPairId, alarmType)
                    return
                }
                if (!thisAlarmPair.hasDefaultAlarm() && thisAlarmPair.isNonRepeating()) thisAlarmPair.deactivate()
                val loc = currentLocation(context)
                if (loc == null) {
                    Log.e("matchesWeatherCriteria", "Last known location is null, alarm will ring")
                    ringAlarm(context, alarmPairId, alarmType)
                    return
                }
                ringIfWeatherCriteriaMatch(context, alarmPairId, loc)
            } else {
                Log.i("AlarmReceiver", "Alarm pair inactive")
                thisAlarmPair.moveWeatherToTomorrow()
                thisAlarmPair.moveDefaultToTomorrow()
            }
        }
    }

    private fun ringAlarm(context: Context, alarmPairId: Int, alarmType: Int) {
        val launchIntent = Intent(context, RingSliderActivity::class.java)
        launchIntent.putExtra("ALARM_PAIR_ID", alarmPairId)
        launchIntent.putExtra("ALARM_TYPE", alarmType)
        launchIntent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        startActivity(context, launchIntent, null)
    }

    private fun currentLocation(context: Context): Location? {
        return try {
            val lastKnownLocation = util.getLastKnownLocation(context)
            Log.e("Last Location", "" + lastKnownLocation?.longitude + ":" + lastKnownLocation?.latitude)
            lastKnownLocation
        } catch (ex: SecurityException) {
            Log.e("", "Security Exception, no location available")
            null
        }
    }

    private fun ringIfWeatherCriteriaMatch(context: Context, alarmPairId: Int, loc: Location) {
        val lat = loc.latitude
        val lon = loc.longitude

        val url = "https://whispering-inlet-50260.herokuapp.com/weatherornot/$lat,$lon"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e("matchesWeatherCriteria", "call to host failed")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()
                val thisAlarmPair = AlarmPairManager.getInstance(context).getAlarmPairByID(alarmPairId)

                if (thisAlarmPair == null) {
                    Log.e("AlarmReceiver", "Alarm Pair null")
                    return
                }
                val thisAlarm = thisAlarmPair?.getWeatherAlarm()
                if (body == null) {
                    Log.e("AlarmReceiver", "Null response body from weather service")
                    thisAlarmPair.moveWeatherToTomorrow()
                } else {
                    Log.e("matchesWeatherCriteria", body)
                    if (thisAlarm == null) {
                        Log.e("AlarmReceiver", "Null alarm error")
                        thisAlarmPair.moveWeatherToTomorrow()
                    } else {
                        val gson = GsonBuilder().create()
                        val currentWeather = gson.fromJson(body, WeatherResponse::class.java)
                        if (thisAlarm.matchesCriteria(currentWeather)) {
                            thisAlarmPair.moveDefaultToTomorrow()
                            ringAlarm(context, alarmPairId, Alarm.ALARM_TYPE_WEATHER)
                        } else {
                            thisAlarmPair.moveWeatherToTomorrow()
                            Log.e("AlarmReceiver", "Criteria not matched, not ringing weatherAlarm")
                            //Toast.makeText(context, "WeatherOrNot: Alarm criteria not met. Sleep in!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}