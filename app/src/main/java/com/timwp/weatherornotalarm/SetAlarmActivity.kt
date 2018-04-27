package com.timwp.weatherornotalarm

import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_set_alarm.*
import java.net.URI
import java.util.*
import kotlin.math.abs

class SetAlarmActivity : AppCompatActivity() {

    lateinit var conditionLabel: TextView
    lateinit var tempLabel: TextView
    lateinit var windLabel: TextView
    lateinit var fogLabel: TextView
    lateinit var repeatLabel: TextView
    lateinit var toneLabel: TextView
    lateinit var calendar: Calendar
    lateinit var rm: RingtoneManager
    lateinit var ringtonePath: Uri
    lateinit var selectedTone: Ringtone

    private var weatherCriteria = IWeatherCriteria(
            "Any",
            "Above",
            "Any",
            "Above",
            "Any",
            "Any",
            "Any"
    )

    val CONDITION_PICKER_OK_RESPONSE = 1
    val TEMP_PICKER_OK_RESPONSE = 2
    val WIND_PICKER_OK_RESPONSE = 3
    val FOG_PICKER_OK_RESPONSE = 4
    val REPEAT_PICKER_OK_RESPONSE = 5
    val TONE_PICKER_OK_RESPONSE = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        conditionLabel = findViewById(R.id.conditions_label)
        tempLabel = findViewById(R.id.temp_label)
        windLabel = findViewById(R.id.wind_label)
        fogLabel = findViewById(R.id.fog_label)
        toneLabel = findViewById(R.id.tone_label)
        calendar = Calendar.getInstance()
        ringtonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val conditionItems = arrayOf("Any", "Rain", "Snow", "Sunny", "Cloudy")
        val tempOpItems = arrayOf("Above", "Below")
        val tempNumItems = arrayOf("Any", "-30", "-25", "-20", "-15", "-10", "-5", "0", "5", "10", "15", "20", "25", "30")
        val windOpItems = arrayOf("Above", "Below")
        val windSpeedItems = arrayOf("Any", "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50")
        val windDirItems = arrayOf("Any", "N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val fogItems = arrayOf("Any", "Foggy", "No fog")
        val repeatItems = arrayOf("Never", "Mondays", "Tuesdays", "Wednesdays", "Thursdays", "Fridays", "Saturdays", "Sundays")

        /*
        conditionSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, conditionItems)
        tempOpSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempOpItems)
        tempNumSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempNumItems)
        windOpSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, windOpItems)
        windSpeedSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, windSpeedItems)
        windDirSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, windDirItems)
        fogSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fogItems)
        */

        toneLabel.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)
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
        var second: Int = Calendar.getInstance().get(Calendar.SECOND) + 3
        if (second > 59) {
            second %= 60
            calendar.set(Calendar.MINUTE, timepicker.minute + 1)
        }
        calendar.set(Calendar.SECOND, second)
        val repeatDays = arrayOf("Never") // TODO: use real info

        val alarmSettings = IAlarmSettings(
                abs((Calendar.getInstance().timeInMillis).toInt()),
                calendar.timeInMillis,
                timepicker.hour,
                timepicker.minute,
                "Vancouver, BC", // TODO: use real info
                weatherCriteria,
                "false",
                repeatDays,
                ringtonePath.toString()
        )

        val alarm = Alarm(alarmSettings, this)
        alarm.set()

        back()
    }

    fun pickConditions(view: View) {
        val launchIntent = Intent(applicationContext, ConditionPicker::class.java)
        startActivityForResult(launchIntent, 1)
    }

    fun pickTemp(view: View) {

    }

    fun pickWind(view: View) {

    }

    fun pickFog(view: View) {

    }

    fun pickRepeat(view: View) {

    }

    fun pickTone(view: View) {
        val tonePickerIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtonePath)
        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, false)
        startActivityForResult(tonePickerIntent, TONE_PICKER_OK_RESPONSE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            TONE_PICKER_OK_RESPONSE -> {
                Log.e("onActivityResult", "toneTicker responded")
                val pickedURI = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                if (pickedURI != null) {
                    ringtonePath = pickedURI
                    toneLabel.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)
                }
            }
            CONDITION_PICKER_OK_RESPONSE -> {
                Log.e("ConditionPicker result", "got a response")
                val pickedCondition = data.getStringExtra("PICKED_CONDITION")
                if (pickedCondition == null) Log.e("SetAlarmAcitivity: OnActivityResult", "Null response data from ConditionPicker")
                else {
                    conditionLabel.text = pickedCondition
                    weatherCriteria.conditions = pickedCondition
                }
            }
            else -> Log.e("SetAlarmAcitivity: OnActivityResult", "Unknown Request Code")
        }
    }
}
