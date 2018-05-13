package com.timwp.weatherornotalarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class CriteriaPickerActivity : AppCompatActivity() {

    private lateinit var returnIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criteria_picker)
        returnIntent = Intent()
    }

    fun onClickConditions(v: View) {
        returnIntent.putExtra("CRITERIA_BROAD_CATEGORY", getString(R.string.conditions))
        done()
    }

    fun onClickTemperature(v: View) {
        returnIntent.putExtra("CRITERIA_BROAD_CATEGORY", getString(R.string.temperature))
        done()
    }

    fun onClickWind(v: View) {
        returnIntent.putExtra("CRITERIA_BROAD_CATEGORY", getString(R.string.wind))
        done()
    }

    private fun done() {
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            setResult(Activity.RESULT_OK, data)
            finish()
        } else {
            val cancelIntent = Intent()
            setResult(Activity.RESULT_CANCELED, cancelIntent)
            finish()
        }
    }
}
