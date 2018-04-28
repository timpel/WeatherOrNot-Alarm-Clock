package com.timwp.weatherornotalarm

import android.app.Activity
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_set_alarm.*
import java.util.*
import kotlin.math.abs

class SetAlarmActivity : AppCompatActivity() {

    lateinit var conditionLabel: TextView
    lateinit var tempLabel: TextView
    lateinit var windLabel: TextView
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
            "C",
            "Above",
            "Any",
            "Any",
            "km"
    )

    val CONDITION_PICKER_REQUEST_CODE = 1
    val TEMP_PICKER_REQUEST_CODE = 2
    val WIND_PICKER_REQUEST_CODE = 3
    val REPEAT_PICKER_REQUEST_CODE = 4
    val TONE_PICKER_REQUEST_CODE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        conditionLabel = findViewById(R.id.conditions_label)
        tempLabel = findViewById(R.id.temp_label)
        windLabel = findViewById(R.id.wind_label)
        toneLabel = findViewById(R.id.tone_label)
        calendar = Calendar.getInstance()
        ringtonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

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
        val launchIntent = Intent(applicationContext, ConditionPickerActivity::class.java)
        startActivityForResult(launchIntent, CONDITION_PICKER_REQUEST_CODE)
    }

    fun pickTemp(view: View) {
        val launchIntent = Intent(applicationContext, TemperaturePickerActivity::class.java)
        launchIntent.putExtra("CURRENT_TEMP_CRITERIA", arrayOf(weatherCriteria.tempOperator,
                weatherCriteria.temp, weatherCriteria.tempUnit))
        startActivityForResult(launchIntent, TEMP_PICKER_REQUEST_CODE)
    }

    fun pickWind(view: View) {
        val launchIntent = Intent(applicationContext, WindPickerActivity::class.java)
        launchIntent.putExtra("CURRENT_WIND_CRITERIA", arrayOf(weatherCriteria.windOperator,
                weatherCriteria.windSpeed, weatherCriteria.windUnit, weatherCriteria.windDirection))
        startActivityForResult(launchIntent, WIND_PICKER_REQUEST_CODE)
    }

    fun pickRepeat(view: View) {

    }

    fun pickTone(view: View) {
        val tonePickerIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtonePath)
        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, false)
        startActivityForResult(tonePickerIntent, TONE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                TONE_PICKER_REQUEST_CODE -> {
                    Log.e("onActivityResult", "TonePicker responded")
                    val pickedURI = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    if (pickedURI != null) {
                        ringtonePath = pickedURI
                        toneLabel.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)
                    }
                }
                CONDITION_PICKER_REQUEST_CODE -> {
                    Log.e("ConditionPickerActivity result", "ConditionPicker responded")
                    val pickedCondition = data.getStringExtra("PICKED_CONDITION")
                    if (pickedCondition == null) Log.e("SetAlarmAcitivity: OnActivityResult", "Null response data from ConditionPickerActivity")
                    else {
                        conditionLabel.text = pickedCondition
                        weatherCriteria.conditions = pickedCondition
                    }
                }
                TEMP_PICKER_REQUEST_CODE -> {
                    Log.e("TemperaturePickerActivity result", "TemperaturePicker responded")
                    val pickedTemperature = data.getStringArrayExtra("PICKED_TEMPERATURE")
                    if (pickedTemperature == null) {
                        tempLabel.text = applicationContext.getString(R.string.any)
                        weatherCriteria.temp = "Any"
                    } else {
                        val displayString = pickedTemperature.joinToString(" ")
                        tempLabel.text = displayString
                        weatherCriteria.tempOperator = pickedTemperature[0]
                        weatherCriteria.temp = pickedTemperature[1]
                        weatherCriteria.tempUnit = pickedTemperature[2].drop(1)
                    }
                }
                WIND_PICKER_REQUEST_CODE -> {
                    Log.e("WindPickerActivity result", "WindPicker responded")
                    val picked = data.getStringArrayExtra("PICKED_WIND")
                    if (picked == null) {
                        windLabel.text = applicationContext.getString(R.string.any)
                        weatherCriteria.windSpeed = "Any"
                        weatherCriteria.windDirection = "Any"
                    } else {
                        val displayString = picked.joinToString(" ")
                        windLabel.text = displayString
                        weatherCriteria.windOperator = picked[0]
                        weatherCriteria.windSpeed = picked[1]
                        weatherCriteria.windUnit = picked[2]
                        weatherCriteria.windDirection = picked[3]
                    }
                }
                else -> Log.e("SetAlarmAcitivity: OnActivityResult", "Unknown Request Code")
            }
        }
    }
}
