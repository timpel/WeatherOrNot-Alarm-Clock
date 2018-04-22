package com.timwp.weatherornotalarm

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_set_alarm.*
import java.time.Instant
import java.util.*
import kotlin.math.abs

class SetAlarmActivity : AppCompatActivity() {

    lateinit var conditionSpinner: Spinner
    lateinit var tempOpSpinner: Spinner
    lateinit var tempNumSpinner: Spinner
    lateinit var windOpSpinner: Spinner
    lateinit var windSpeedSpinner: Spinner
    lateinit var windDirSpinner: Spinner
    lateinit var fogSpinner: Spinner
    lateinit var repeatSpinner: Spinner
    lateinit var toneSpinner: Spinner
    lateinit var snoozeSpinner: Spinner
    lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        conditionSpinner = findViewById(R.id.condition_spinner)
        tempOpSpinner = findViewById(R.id.temp_op_spinner)
        tempNumSpinner = findViewById(R.id.temp_num_spinner)
        windOpSpinner = findViewById(R.id.wind_op_spinner)
        windSpeedSpinner = findViewById(R.id.wind_speed_spinner)
        windDirSpinner = findViewById(R.id.wind_dir_spinner)
        fogSpinner = findViewById(R.id.fog_spinner)
        repeatSpinner = findViewById(R.id.repeat_spinner)
        toneSpinner = findViewById(R.id.tone_spinner)
        snoozeSpinner = findViewById(R.id.snooze_spinner)

        calendar = Calendar.getInstance()

        val conditionItems = arrayOf("Any", "Rain", "Snow", "Sunny", "Cloudy")
        val tempOpItems = arrayOf("Above", "Below")
        val tempNumItems = arrayOf("Any", "-30", "-25", "-20", "-15", "-10", "-5", "0", "5", "10", "15", "20", "25", "30")
        val windOpItems = arrayOf("Above", "Below")
        val windSpeedItems = arrayOf("Any", "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50")
        val windDirItems = arrayOf("Any", "N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val fogItems = arrayOf("Any", "Foggy", "No fog")
        val repeatItems = arrayOf("Never", "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays")
        val toneItems = arrayOf("Cool ringtone 1", "Cool ringtone 2")
        val snoozeItems = arrayOf("Off", "5 minutes", "10 minutes", "15 minutes")

        conditionSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conditionItems)
        tempOpSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempOpItems)
        tempNumSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempNumItems)
        windOpSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, windOpItems)
        windSpeedSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, windSpeedItems)
        windDirSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, windDirItems)
        fogSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fogItems)
        repeatSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, repeatItems)
        toneSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, toneItems)
        snoozeSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, snoozeItems)
    }

    fun back(view: View) {
        back()
    }

    fun back() {
        val launchIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(launchIntent)
    }

    fun setAlarm(view: View) {
        calendar.set(Calendar.HOUR_OF_DAY, timepicker.hour)
        calendar.set(Calendar.MINUTE, timepicker.minute)
        calendar.set(Calendar.SECOND, 0)
        val repeatDays = arrayOf("Never") // TODO: use real info

        val weatherCriteria = IWeatherCriteria (
            conditionSpinner.selectedItem as String,
            tempNumSpinner.selectedItem as String,
            tempOpSpinner.selectedItem as String,
            windOpSpinner.selectedItem as String,
            windSpeedSpinner.selectedItem as String,
            windDirSpinner.selectedItem as String,
            fogSpinner.selectedItem as String
        )
        val alarmSettings = IAlarmSettings (
            abs((Calendar.getInstance().timeInMillis).toInt()),
            calendar.timeInMillis,
            timepicker.hour,
            timepicker.minute,
            "Vancouver, BC", // TODO: use real info
            weatherCriteria,
            "false",
            snoozeSpinner.selectedItem as String,
            repeatDays
        )

        val alarm = Alarm(alarmSettings, this)
        alarm.set()

        back()
    }
}
