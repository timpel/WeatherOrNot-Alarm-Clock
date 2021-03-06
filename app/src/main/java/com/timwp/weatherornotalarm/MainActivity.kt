package com.timwp.weatherornotalarm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.amazonaws.mobile.client.AWSStartupResult
import com.amazonaws.mobile.client.AWSStartupHandler
import com.amazonaws.mobile.client.AWSMobileClient



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AWSMobileClient.getInstance().initialize(this) {
            Log.d("WeatherOrNot", "AWSMobileClient instantiated")
        }.execute()

        val launchIntent = Intent(applicationContext, AlarmListActivity::class.java)
        startActivity(launchIntent)
    }

    fun addAlarm(view: View) {
        val launchIntent = Intent(applicationContext, SetMultiAlarmActivity::class.java)
        launchIntent.putExtra("DEFAULT_CURRENTLY_SET", false)
        launchIntent.putExtra("WEATHER_CURRENTLY_SET", false)
        launchIntent.putExtra("CURRENT_REPEATS", BooleanArray(7))
        launchIntent.putExtra("CURRENT_WEATHER_CRITERIA", Array(8, { _ -> "" }))
        startActivity(launchIntent)
        Log.e("Logging", "in SetMultiAlarm")
    }
}
