package com.timwp.weatherornotalarm

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.NumberPicker
import android.widget.RadioGroup

class WindPickerActivity : AppCompatActivity() {

    lateinit var WIND_OPERATIONS: Array<String>
    var WIND_MIN: Int = Int.MIN_VALUE
    var WIND_MAX: Int = Int.MAX_VALUE
    lateinit var WIND_UNITS: Array<String>
    lateinit var WIND_DIRECTIONS: Array<String>
    var WIND_STEP: Int = 5

    lateinit var windPickers: ConstraintLayout
    lateinit var windRadios: RadioGroup
    lateinit var windOpPicker: NumberPicker
    lateinit var windNumPicker: NumberPicker
    lateinit var windUnitPicker: NumberPicker
    lateinit var windDirectionPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wind_picker)

        val currentWindPicks = intent.getStringArrayExtra("CURRENT_WIND_CRITERIA")

        WIND_OPERATIONS = arrayOf("Above", "Below")
        WIND_MIN = 0
        WIND_MAX = 80
        WIND_UNITS = arrayOf(applicationContext.getString(R.string.km), applicationContext.getString(R.string.mph))
        WIND_DIRECTIONS = arrayOf("-", "N", "NW", "W", "SW", "S", "SE", "E", "NE")


        initializeRadios(currentWindPicks)
        initializePickers(currentWindPicks)
    }

    fun onClickOk(view: View) {
        val returnIntent = Intent()
        val windCriteria = gatherCriteria()
        returnIntent.putExtra("PICKED_WIND", windCriteria)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onClickCancel(view: View) {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun gatherCriteria(): Array<String>? {
        return if (windRadios.checkedRadioButtonId == R.id.any_radio) null else {
            arrayOf(windOpPicker.displayedValues[windOpPicker.value],
                    windNumPicker.displayedValues[windNumPicker.value],
                    windUnitPicker.displayedValues[windUnitPicker.value],
                    windDirectionPicker.displayedValues[windDirectionPicker.value])
        }
    }

    private fun initializeRadios(currentWindPicks: Array<String>) {
        windRadios = findViewById(R.id.windRadios)
        if (currentWindPicks[1] != applicationContext.getString(R.string.any)) {
            windRadios.check(R.id.filter_radio)
        }
        windRadios.setOnCheckedChangeListener({ _, checkedId ->
            when (checkedId) {
                R.id.any_radio -> {
                    Log.e("radioGroup onCheckedChangedListener", "Any checked")
                    hidePickers()
                }
                R.id.filter_radio -> {
                    Log.e("radioGroup onCheckedChangedListener", "Filter checked")
                    revealPickers()
                }
                else -> {
                    Log.e("radioGroup onCheckedChangedListener", "checkedId not recognized")
                }
            }
        })
    }

    private fun initializePickers(currentWindPicks: Array<String>) {
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

        if (currentWindPicks[1] != applicationContext.getString(R.string.any)) {
            windPickers.visibility = View.VISIBLE
            windOpPicker.value = if (currentWindPicks[0] == "Above") 0 else 1
            windNumPicker.value = (currentWindPicks[1].toInt() - WIND_MIN) / WIND_STEP
            windUnitPicker.value = if (currentWindPicks[2] == "C") 0 else 1
            windDirectionPicker.value = WIND_DIRECTIONS.indexOf(currentWindPicks[3])
        }
        else {
            windPickers.visibility = View.INVISIBLE
        }
    }

    private fun revealPickers() {
        // get the center for the clipping circle
        val cx = windPickers.width / 2
        val cy = windPickers.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(windPickers, cx, cy, 0f, finalRadius)

        // make the view visible and start the animation
        windPickers.visibility = View.VISIBLE
        anim.start()
    }

    private fun hidePickers() {
        // get the center for the clipping circle
        val cx = windPickers.getWidth() / 2
        val cy = windPickers.getHeight() / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(windPickers, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                windPickers.visibility = View.INVISIBLE
            }
        })

        // start the animation
        anim.start()
    }
}
