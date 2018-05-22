package com.timwp.weatherornotalarm

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_ring.*


class RingFragment: Fragment() {

    private var thisAlarm: Alarm? = null
    private var snoozeIt = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: ViewGroup = inflater.inflate(
                R.layout.fragment_ring, container, false) as ViewGroup

        val stopButton = view.findViewById(R.id.stop_ring) as Button

        stopButton.setOnClickListener {
            snoozeIt = false
            val launchIntent = Intent(activity?.applicationContext, MainActivity::class.java)
            startActivity(launchIntent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        val alarmPairID = activity?.intent?.getIntExtra("ALARM_PAIR_ID", -1)
        val alarmType = activity?.intent?.getIntExtra("ALARM_TYPE", -1)

        if (context != null) {
            val alarmPairManager = AlarmPairManager.getInstance(context!!)
            alarmPairManager.update(context!!)


            thisAlarm = alarmPairManager.getAlarmPairByID(alarmPairID!!)?.getAlarmByType(alarmType!!)
            if (thisAlarm == null) {
                Log.e("RingSlider", "Alarm not in alarm pair manager")
                return
            }

            if (alarmType == Alarm.ALARM_TYPE_WEATHER) {
                default_alarm_ring_label.visibility = View.GONE
                weather_icon_ring.visibility = View.VISIBLE
                criteria_label_ring.visibility = View.VISIBLE

                val weatherCriteria = thisAlarm!!.getWeatherCriteria()

                val criteriaStringArray = arrayOf(
                        weatherCriteria.tempOperator,
                        weatherCriteria.temp,
                        if (weatherCriteria.tempUnit != "") '\u00B0' + weatherCriteria.tempUnit else "",
                        weatherCriteria.windOperator,
                        weatherCriteria.windSpeed, weatherCriteria.windUnit, weatherCriteria.windDirection)
                val criteriaString = criteriaStringArray.joinToString(" ").replace("-", "").trim()
                criteria_label_ring.text = criteriaString

                when {
                    weatherCriteria.conditions == "Clear" -> {
                        weather_icon_ring.setImageResource(R.drawable.sun_icon)
                    }
                    weatherCriteria.conditions == "Cloudy" -> {
                        weather_icon_ring.setImageResource(R.drawable.cloud_icon)
                    }
                    weatherCriteria.conditions == "Raining" -> {
                        weather_icon_ring.setImageResource(R.drawable.rain_icon)
                    }
                    weatherCriteria.conditions == "Snowing" -> {
                        weather_icon_ring.setImageResource(R.drawable.snow_icon)
                    }
                    weatherCriteria.tempOperator == "Above" -> {
                        weather_icon_ring.setImageResource(R.drawable.temp_above_icon)
                    }
                    weatherCriteria.tempOperator == "Below" -> {
                        weather_icon_ring.setImageResource(R.drawable.temp_below_icon)
                    }
                    weatherCriteria.windOperator != "" -> {
                        weather_icon_ring.setImageResource(R.drawable.wind_icon)
                    }
                    else -> {
                        weather_icon_ring.visibility = View.INVISIBLE
                    }
                }
            } else {
                default_alarm_ring_label.visibility = View.VISIBLE
                weather_icon_ring.visibility = View.INVISIBLE
                criteria_label_ring.visibility = View.INVISIBLE
            }

            thisAlarm?.ring()
        }
    }

    override fun onPause() {
        super.onPause()
        thisAlarm?.stop()
        if (snoozeIt) thisAlarm?.snooze()
        else thisAlarm?.setForTomorrow()
    }
}
