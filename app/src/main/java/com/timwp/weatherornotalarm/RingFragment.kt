package com.timwp.weatherornotalarm

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button


class RingFragment: Fragment() {

    private var thisAlarm: Alarm? = null
    private var snoozeIt = true

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: ViewGroup = inflater!!.inflate(
                R.layout.fragment_ring, container, false) as ViewGroup

        val stopButton = view.findViewById(R.id.stop_ring) as Button

        stopButton.setOnClickListener {
            snoozeIt = false
            val launchIntent = Intent(activity.applicationContext, MainActivity::class.java)
            startActivity(launchIntent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Get alarm info
        val alarmID = activity.intent.getIntExtra("ALARM_ID", -1)
        if (alarmID == -1) {
            Log.e("RingSlider", "Couldn't get alarm ID from intent")
            return
        }
        val localAlarmManager = LocalAlarmManager.getInstance(activity.applicationContext)
        localAlarmManager.update(activity.applicationContext)
        thisAlarm = localAlarmManager.getAlarmByID(alarmID)
        if (thisAlarm == null) {
            Log.e("RingSlider", "Alarm not in local alarm manager")
            return
        }
        thisAlarm!!.ring()
    }

    override fun onPause() {
        super.onPause()
        thisAlarm!!.stop()
        if (snoozeIt) thisAlarm!!.snooze()
    }
}
