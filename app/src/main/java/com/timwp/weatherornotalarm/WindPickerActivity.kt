package com.timwp.weatherornotalarm

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.NumberPicker

class WindPickerActivity : AppCompatActivity() {

    private lateinit var WIND_OPERATIONS: Array<String>
    private var WIND_MIN: Int = Int.MIN_VALUE
    private var WIND_MAX: Int = Int.MAX_VALUE
    private lateinit var WIND_UNITS: Array<String>
    private lateinit var WIND_DIRECTIONS: Array<String>
    private var WIND_STEP: Int = 5

    private lateinit var windPickers: ConstraintLayout
    lateinit var windOpPicker: NumberPicker
    lateinit var windNumPicker: NumberPicker
    lateinit var windUnitPicker: NumberPicker
    lateinit var windDirectionPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wind_picker)

        val currentWindPicks: Array<String>? = intent.getStringArrayExtra("CURRENT_WIND_CRITERIA")

        WIND_OPERATIONS = arrayOf("Above", "Below")
        WIND_MIN = 0
        WIND_MAX = 80
        WIND_UNITS = arrayOf(applicationContext.getString(R.string.km), applicationContext.getString(R.string.mph))
        WIND_DIRECTIONS = arrayOf("-", "N", "NW", "W", "SW", "S", "SE", "E", "NE")


        initializePickers(currentWindPicks)
    }

    fun onClickOk(view: View) {
        val returnIntent = Intent()
        val windCriteria = gatherCriteria()
        returnIntent.putExtra("PICKED_WIND", windCriteria)
        returnIntent.putExtra("CRITERIA_STRING", windCriteria?.joinToString(" ") ?: "None")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onClickCancel(view: View) {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun gatherCriteria(): Array<String>? {
            return arrayOf(windOpPicker.displayedValues[windOpPicker.value],
                    windNumPicker.displayedValues[windNumPicker.value],
                    windUnitPicker.displayedValues[windUnitPicker.value],
                    windDirectionPicker.displayedValues[windDirectionPicker.value])
    }

    private fun initializePickers(currentWindPicks: Array<String>?) {
        windPickers = findViewById(R.id.windPickerGroup)
        windOpPicker = findViewById(R.id.windOpPicker)
        windNumPicker = findViewById(R.id.windNumPicker)
        windUnitPicker = findViewById(R.id.windUnitPicker)
        windDirectionPicker = findViewById(R.id.windDirectionPicker)

        windOpPicker.minValue = 0
        windOpPicker.maxValue = WIND_OPERATIONS.size - 1
        windOpPicker.displayedValues = WIND_OPERATIONS
        windOpPicker.value = 0
        windOpPicker.wrapSelectorWheel = false

        val displayNums = arrayOfNulls<String>((WIND_MAX - WIND_MIN) / WIND_STEP)
        for (i in displayNums.indices) {
            displayNums[i] = ((i * WIND_STEP) + WIND_MIN).toString()
        }
        windNumPicker.minValue = 0
        windNumPicker.maxValue = displayNums.size - 1
        windNumPicker.displayedValues = displayNums
        windNumPicker.value = (0 - WIND_MIN) / WIND_STEP
        windNumPicker.wrapSelectorWheel = false


        windUnitPicker.minValue = 0
        windUnitPicker.maxValue = WIND_UNITS.size - 1
        windUnitPicker.displayedValues = WIND_UNITS
        windUnitPicker.value = 0
        windUnitPicker.wrapSelectorWheel = false

        windDirectionPicker.minValue = 0
        windDirectionPicker.maxValue = WIND_DIRECTIONS.size - 1
        windDirectionPicker.displayedValues = WIND_DIRECTIONS
        windDirectionPicker.value = 0
        windDirectionPicker.wrapSelectorWheel = false

        windPickers.visibility = View.VISIBLE

        if (currentWindPicks != null) {
            Log.e("windPicker", "" + currentWindPicks[0] + " " + currentWindPicks[1] + " " + currentWindPicks[2] + " " + currentWindPicks[3])
            windOpPicker.value = if (currentWindPicks[0] == "Above") 0 else 1
            windNumPicker.value = (currentWindPicks[1].toInt() - WIND_MIN) / WIND_STEP
            windUnitPicker.value = if (currentWindPicks[2] == getString(R.string.km)) 0 else 1
            windDirectionPicker.value = WIND_DIRECTIONS.indexOf(currentWindPicks[3])
        }
    }
}
