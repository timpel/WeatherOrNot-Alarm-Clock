package com.timwp.weatherornotalarm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.support.constraint.ConstraintLayout
import android.app.Activity
import android.content.Intent
import android.widget.NumberPicker


class TemperaturePickerActivity : AppCompatActivity() {

    private lateinit var TEMP_OPERATIONS: Array<String>
    private var TEMP_MIN: Int = Int.MIN_VALUE
    private var TEMP_MAX: Int = Int.MAX_VALUE
    private lateinit var TEMP_UNITS: Array<String>
    private var TEMP_STEP: Int = 5

    private lateinit var tempPickers: ConstraintLayout
    lateinit var tempOpPicker: NumberPicker
    lateinit var tempNumPicker: NumberPicker
    lateinit var tempUnitPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_picker)

        val currentTempPicks: Array<String>? = intent.getStringArrayExtra("CURRENT_TEMP_CRITERIA")

        TEMP_OPERATIONS = arrayOf("Above", "Below")
        TEMP_MIN = -50
        TEMP_MAX = 121
        TEMP_UNITS = arrayOf(applicationContext.getString(R.string.degree_c), applicationContext.getString(R.string.degree_f))

        initializePickers(currentTempPicks)
    }

    fun onClickOk(view: View) {
        val returnIntent = Intent()
        val tempCriteria = gatherCriteria()
        returnIntent.putExtra("PICKED_TEMPERATURE", tempCriteria)
        returnIntent.putExtra("CRITERIA_STRING", tempCriteria?.joinToString(" ") ?: "None")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onClickCancel(view: View) {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun gatherCriteria(): Array<String>? {
        return arrayOf(tempOpPicker.displayedValues[tempOpPicker.value],
                tempNumPicker.displayedValues[tempNumPicker.value],
                tempUnitPicker.displayedValues[tempUnitPicker.value])
    }

    private fun initializePickers(currentTempPicks: Array<String>?) {
        tempPickers = findViewById(R.id.tempPickerGroup)
        tempOpPicker = findViewById(R.id.tempOpPicker)
        tempNumPicker = findViewById(R.id.tempNumPicker)
        tempUnitPicker = findViewById(R.id.tempUnitPicker)

        tempOpPicker.minValue = 0
        tempOpPicker.maxValue = TEMP_OPERATIONS.size - 1
        tempOpPicker.displayedValues = TEMP_OPERATIONS
        tempOpPicker.value = 0
        tempOpPicker.wrapSelectorWheel = false

        val displayNums = arrayOfNulls<String>((TEMP_MAX - TEMP_MIN) / TEMP_STEP)
        for (i in displayNums.indices) {
            displayNums[i] = ((i * TEMP_STEP) + TEMP_MIN).toString()
        }
        tempNumPicker.minValue = 0
        tempNumPicker.maxValue = displayNums.size - 1
        tempNumPicker.displayedValues = displayNums
        tempNumPicker.value = (0 - TEMP_MIN) / TEMP_STEP
        tempNumPicker.wrapSelectorWheel = false


        tempUnitPicker.minValue = 0
        tempUnitPicker.maxValue = TEMP_UNITS.size - 1
        tempUnitPicker.displayedValues = TEMP_UNITS
        tempUnitPicker.value = 0
        tempUnitPicker.wrapSelectorWheel = false


        tempPickers.visibility = View.VISIBLE

        if (currentTempPicks != null) {
            tempOpPicker.value = if (currentTempPicks[0] == "Above") 0 else 1
            tempNumPicker.value = (currentTempPicks[1].toInt() - TEMP_MIN) / TEMP_STEP
            tempUnitPicker.value = if (currentTempPicks[2] == "C") 0 else 1
        }
    }
}
