package com.timwp.weatherornotalarm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.time.Instant
import java.util.*

class MainActivity : AppCompatActivity() {
    val genericCriteria = WeatherCriteria("Rain", 20, 5, 10, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setAlarm()
    }
    fun setAlarm() {
        val alarm = Alarm(this, Date.from(Instant.now()), "Vancouver, BC", genericCriteria, false, 10)
        alarm.set()
    }
}
