package com.timwp.weatherornotalarm

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TimePicker

class SetSingleAlarmActivity : AppCompatActivity() {

    private lateinit var setAlarmButton: Button
    private lateinit var noAlarmButton: Button
    private lateinit var alarmTimePicker: TimePicker
    private var alarmType: Int = -1
    private var currentlyPicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_single_alarm)

        setAlarmButton = findViewById(R.id.set_alarm_button)
        noAlarmButton = findViewById(R.id.no_alarm_button)
        alarmTimePicker = findViewById(R.id.alarm_time_picker)

        alarmType = intent.getIntExtra("ALARM_TYPE", -1)
        currentlyPicked = intent.getBooleanExtra("CURRENTLY_PICKED", false)

        initializePicker()

        when (alarmType) {
            Alarm.ALARM_TYPE_DEFAULT -> {
                noAlarmButton.text = getString(R.string.no_default)
            }
            Alarm.ALARM_TYPE_WEATHER -> {
                noAlarmButton.text = getString(R.string.no_weather)
            }
        }
    }

    private fun initializePicker() {
        if (currentlyPicked) {
            alarmTimePicker.hour = intent.getIntExtra("HOUR", -1)
            alarmTimePicker.minute = intent.getIntExtra("MINUTE", -1)
        }
    }

    fun onClickSet(view: View) {
        val returnIntent = Intent()
        returnIntent.putExtra("ALARM_SET", true)
        returnIntent.putExtra("ALARM_TYPE", alarmType)
        returnIntent.putExtra("HOUR", alarmTimePicker.hour)
        returnIntent.putExtra("MINUTE", alarmTimePicker.minute)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onClickNone(view: View) {
        val returnIntent = Intent()
        returnIntent.putExtra("ALARM_SET", false)
        returnIntent.putExtra("ALARM_TYPE", alarmType)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}
