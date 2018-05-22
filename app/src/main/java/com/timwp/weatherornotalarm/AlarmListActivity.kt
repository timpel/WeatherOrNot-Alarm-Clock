package com.timwp.weatherornotalarm

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_alarm_list.*
import android.provider.Settings
import android.support.v4.content.ContextCompat


class AlarmListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)
        AlarmPairManager.getInstance(applicationContext).update(applicationContext)
        alarm_recycler.layoutManager = LinearLayoutManager(this)
        alarm_recycler.adapter = AlarmPairAdapter(AlarmPairManager.getInstance(applicationContext).listAlarmPairs())
        checkPermissions()
    }

    private fun checkPermissions() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1)

    }

    fun addAlarmPair(view: View) {
        val launchIntent = Intent(applicationContext, SetMultiAlarmActivity::class.java)
        launchIntent.putExtra("DEFAULT_CURRENTLY_SET", false)
        launchIntent.putExtra("WEATHER_CURRENTLY_SET", false)
        launchIntent.putExtra("CURRENT_REPEATS", BooleanArray(7))
        launchIntent.putExtra("CURRENT_WEATHER_CRITERIA", Array(8, { _ -> "" }))
        startActivity(launchIntent)
    }

    fun openAboutPage(view: View) {
        val launchIntent = Intent(applicationContext, AboutPageActivity::class.java)
        startActivity(launchIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            alertBuilder.setMessage("WeatherOrNot needs accurate weather data to wake you up at the right time, and using your device location makes that possible. To use WeatherOrNot Alarm please enable the 'Location' permission on the next page.")
                    .setPositiveButton("OK", { dialogInterface, i ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    })
            alertBuilder.create().show()
        }
    }
}
