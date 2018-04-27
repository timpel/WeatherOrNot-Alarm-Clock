package com.timwp.weatherornotalarm

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.NumberPicker
import android.widget.RadioGroup


abstract class PickerActivity : AppCompatActivity() {

    lateinit var OPERATIONS: Array<String>
    var MIN_VAL: Int = Int.MIN_VALUE
    var MAX_VAL: Int = Int.MAX_VALUE
    lateinit var UNITS: Array<String>
    var STEP: Int = 1

    lateinit var pickers: ConstraintLayout
    lateinit var radioGroup: RadioGroup
    lateinit var opPicker: NumberPicker
    lateinit var numPicker: NumberPicker
    lateinit var unitPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temperature_picker)

        val currentPicks = intent.getStringArrayExtra("CURRENT_CRITERIA")

        setOperations()
        setMinVal()
        setMaxVal()
        setUnits()

        initializeRadios(currentPicks)
        initializePickers(currentPicks)
    }

    fun setMaxVal() {
        MAX_VAL = 121
    }

    fun setMinVal() {
        MIN_VAL = -50
    }

    fun setOperations() {
        OPERATIONS = arrayOf("Above", "Below")
    }

    fun setUnits() {
        UNITS = arrayOf(applicationContext.getString(R.string.degree_c), applicationContext.getString(R.string.degree_f))
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
        return if (radioGroup.checkedRadioButtonId == R.id.any_radio) null else {
            arrayOf(opPicker.displayedValues[opPicker.value],
                    numPicker.displayedValues[numPicker.value],
                    unitPicker.displayedValues[unitPicker.value])
        }
    }

    private fun initializeRadios(currentTempPicks: Array<String>) {
        radioGroup = findViewById(R.id.tempRadios)
        if (currentTempPicks[1] != applicationContext.getString(R.string.any)) {
            radioGroup.check(R.id.filter_radio)
        }
        radioGroup.setOnCheckedChangeListener({ _, checkedId ->
            when (checkedId) {
                R.id.any_radio -> {
                    Log.e("radioGroup onCheckedChangedListener", "Any checked")
                    hideTempPickers()
                }
                R.id.filter_radio -> {
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
        pickers = findViewById(R.id.tempPickerGroup)
        opPicker = findViewById(R.id.tempOpPicker)
        numPicker = findViewById(R.id.tempNumPicker)
        unitPicker = findViewById(R.id.tempUnitPicker)

        opPicker.minValue = 0
        opPicker.maxValue = OPERATIONS.size - 1
        opPicker.displayedValues = OPERATIONS
        opPicker.value = 0
        opPicker.wrapSelectorWheel = false

        val displayNums = arrayOfNulls<String>((MAX_VAL - MIN_VAL) / STEP)
        for (i in displayNums.indices) {
            displayNums[i] = ((i * STEP) + MIN_VAL).toString()
        }
        numPicker.minValue = 0
        numPicker.maxValue = displayNums.size - 1
        numPicker.displayedValues = displayNums
        numPicker.value = (0 - MIN_VAL) / STEP
        numPicker.wrapSelectorWheel = false


        unitPicker.minValue = 0
        unitPicker.maxValue = UNITS.size - 1
        unitPicker.displayedValues = UNITS
        unitPicker.value = 0
        unitPicker.wrapSelectorWheel = false

        if (currentTempPicks[1] != applicationContext.getString(R.string.any)) {
            pickers.visibility = View.VISIBLE
            opPicker.value = if (currentTempPicks[0] == "Above") 0 else 1
            numPicker.value = (currentTempPicks[1].toInt() - MIN_VAL) / STEP
            unitPicker.value = if (currentTempPicks[2] == "C") 0 else 1
        }
        else {
            pickers.visibility = View.INVISIBLE
        }
    }

    private fun revealTempPickers() {
        // get the center for the clipping circle
        val cx = pickers.width / 2
        val cy = pickers.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(pickers, cx, cy, 0f, finalRadius)

        // make the view visible and start the animation
        pickers.visibility = View.VISIBLE
        anim.start()
    }

    private fun hideTempPickers() {
        // get the center for the clipping circle
        val cx = pickers.getWidth() / 2
        val cy = pickers.getHeight() / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(pickers, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                pickers.visibility = View.INVISIBLE
            }
        })

        // start the animation
        anim.start()
    }
}
