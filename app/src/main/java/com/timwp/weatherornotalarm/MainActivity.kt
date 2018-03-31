package com.timwp.weatherornotalarm

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Instant
import java.util.*

class MainActivity : AppCompatActivity() {
    val genericCriteria: IWeatherCriteria = object: IWeatherCriteria {
        override val conditions: String = "Rain"
        override val temp: String = "20"
        override val tempOperator: String = "BELOW"
        override val windOperator: String = "ABOVE"
        override val windSpeed: String = "5"
        override val windDirection: String = "10"
        override val fog: String = "No fog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun addAlarm(view: View) {
        val launchIntent = Intent(applicationContext, SetAlarmActivity::class.java)
        startActivity(launchIntent)
        Log.e("Logging", "in addAlarm")
    }
}
