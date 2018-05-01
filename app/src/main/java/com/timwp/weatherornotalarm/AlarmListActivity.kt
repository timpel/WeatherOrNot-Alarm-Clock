package com.timwp.weatherornotalarm

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_alarm_list.*

class AlarmListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)
        AlarmPairManager.getInstance(applicationContext).update(applicationContext)
        alarm_recycler.layoutManager = LinearLayoutManager(this)
        alarm_recycler.adapter = AlarmPairAdapter(AlarmPairManager.getInstance(applicationContext).listAlarmPairs())
    }

    fun addAlarmPair(view: View) {
        val launchIntent = Intent(applicationContext, SetMultiAlarmActivity::class.java)
        launchIntent.putExtra("DEFAULT_CURRENTLY_SET", false)
        launchIntent.putExtra("WEATHER_CURRENTLY_SET", false)
        launchIntent.putExtra("CURRENT_REPEATS", BooleanArray(7))
        launchIntent.putExtra("CURRENT_WEATHER_CRITERIA", Array(8, { _ -> "" }))
        startActivity(launchIntent)
        Log.e("Logging", "in SetMultiAlarm")
    }
}
