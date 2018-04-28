package com.timwp.weatherornotalarm

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import android.view.ViewAnimationUtils
import android.animation.Animator
import android.support.constraint.ConstraintLayout
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.widget.NumberPicker


class TemperaturePickerActivity : AppCompatActivity() {

    lateinit var TEMP_OPERATIONS: Array<String>
    var TEMP_MIN: Int = Int.MIN_VALUE
    var TEMP_MAX: Int = Int.MAX_VALUE
    lateinit var TEMP_UNITS: Array<String>
    var TEMP_STEP: Int = 5

    lateinit var tempPickers: ConstraintLayout
    lateinit var tempRadios: RadioGroup
    lateinit var tempOpPicker: NumberPicker
    lateinit var tempNumPicker: NumberPicker
    lateinit var tempUnitPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_picker)

        val currentTempPicks = intent.getStringArrayExtra("CURRENT_TEMP_CRITERIA")

        TEMP_OPERATIONS = arrayOf("Above", "Below")
        TEMP_MIN = -50
        TEMP_MAX = 121
        TEMP_UNITS = arrayOf(applicationContext.getString(R.string.degree_c), applicationContext.getString(R.string.degree_f))


        initializeRadios(currentTempPicks)
        initializePickers(currentTempPicks)
    }

    fun onClickOk(view: View) {
        val returnIntent = Intent()
        val tempCriteria = gatherCriteria()
        returnIntent.putExtra("PICKED_TEMPERATURE", tempCriteria)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onClickCancel(view: View) {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun gatherCriteria(): Array<String>? {
        return if (tempRadios.checkedRadioButtonId == R.id.norepeat_radio) null else {
            arrayOf(tempOpPicker.displayedValues[tempOpPicker.value],
                    tempNumPicker.displayedValues[tempNumPicker.value],
                    tempUnitPicker.displayedValues[tempUnitPicker.value])
        }
    }

    private fun initializeRadios(currentTempPicks: Array<String>) {
        tempRadios = findViewById(R.id.tempRadios)
        if (currentTempPicks[1] != applicationContext.getString(R.string.any)) {
            tempRadios.check(R.id.repeat_radio)
        }
        tempRadios.setOnCheckedChangeListener({ _, checkedId ->
            when (checkedId) {
                R.id.norepeat_radio -> {
                    Log.e("radioGroup onCheckedChangedListener", "Any checked")
                    hideTempPickers()
                }
                R.id.repeat_radio -> {
                    Log.e("radioGroup onCheckedChangedListener", "Filter checked")
                    revealTempPickers()
                }
                else -> {
                    Log.e("radioGroup onCheckedChangedListener", "checkedId not recognized")
                }
            }
        })
    }

    private fun initializePickers(currentTempPicks: Array<String>) {
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

        if (currentTempPicks[1] != applicationContext.getString(R.string.any)) {
            tempPickers.visibility = View.VISIBLE
            tempOpPicker.value = if (currentTempPicks[0] == "Above") 0 else 1
            tempNumPicker.value = (currentTempPicks[1].toInt() - TEMP_MIN) / TEMP_STEP
            tempUnitPicker.value = if (currentTempPicks[2] == "C") 0 else 1
        }
        else {
            tempPickers.visibility = View.INVISIBLE
        }
    }

    private fun revealTempPickers() {
        // get the center for the clipping circle
        val cx = tempPickers.width / 2
        val cy = tempPickers.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(tempPickers, cx, cy, 0f, finalRadius)

        // make the view visible and start the animation
        tempPickers.visibility = View.VISIBLE
        anim.start()
    }

    private fun hideTempPickers() {
        // get the center for the clipping circle
        val cx = tempPickers.getWidth() / 2
        val cy = tempPickers.getHeight() / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(tempPickers, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                tempPickers.visibility = View.INVISIBLE
            }
        })

        // start the animation
        anim.start()
    }
}
