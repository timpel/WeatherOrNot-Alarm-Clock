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
import android.widget.CheckBox
import android.widget.RadioGroup

class RepeatPickerActivity : AppCompatActivity() {

    lateinit var boxLayout: ConstraintLayout
    lateinit var repeatRadios: RadioGroup
    private lateinit var boxArray: Array<CheckBox>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repeat_picker)

        val currentRepeats = intent.getBooleanArrayExtra("CURRENT_REPEATS")
        boxArray = arrayOf(findViewById(R.id.sunday),
                findViewById(R.id.monday),
                findViewById(R.id.tuesday),
                findViewById(R.id.wednesday),
                findViewById(R.id.thursday),
                findViewById(R.id.friday),
                findViewById(R.id.saturday))

        initializeRadios(currentRepeats)
        initializeBoxes(currentRepeats)
    }

    fun onClickOk(view: View) {
        val returnIntent = Intent()
        val repeats = gatherRepeats()
        returnIntent.putExtra("PICKED_REPEATS", repeats)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onClickCancel(view: View) {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }

    private fun gatherRepeats(): BooleanArray {
        if (repeatRadios.checkedRadioButtonId == R.id.norepeat_radio) {
            return BooleanArray(7)
        }
        return boxArray.map {
            it.isChecked
        }.toBooleanArray()
    }

    private fun initializeRadios(currentRepeats: BooleanArray) {
        repeatRadios = findViewById(R.id.repeatRadios)
        if (!currentRepeats.contains(true)) {
            repeatRadios.check(R.id.norepeat_radio)
        } else {
            repeatRadios.check(R.id.repeat_radio)
        }
        repeatRadios.setOnCheckedChangeListener({ _, checkedId ->
            when (checkedId) {
                R.id.norepeat_radio -> {
                    Log.e("radios onCheckedChanged", "No repeats checked")
                    hidePickers()
                }
                R.id.repeat_radio -> {
                    Log.e("radios onCheckedChanged", "Repeats checked")
                    revealPickers()
                }
                else -> {
                    Log.e("radios onCheckedChanged", "checkedId not recognized")
                }
            }
        })
    }

    private fun initializeBoxes(currentRepeats: BooleanArray) {
        boxLayout = findViewById(R.id.day_checkboxes)
        if (!currentRepeats.contains(true)) {
            boxLayout.visibility = View.INVISIBLE
        } else {
            boxLayout.visibility = View.VISIBLE
        }
        for ((index, value) in currentRepeats.withIndex()) {
            boxArray[index].isChecked = value
        }
    }

    private fun revealPickers() {
        // get the center for the clipping circle
        val cx = boxLayout.width / 2
        val cy = boxLayout.height / 2

        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(boxLayout, cx, cy, 0f, finalRadius)

        // make the view visible and start the animation
        boxLayout.visibility = View.VISIBLE
        anim.start()
    }

    private fun hidePickers() {
        // get the center for the clipping circle
        val cx = boxLayout.width / 2
        val cy = boxLayout.height / 2

        // get the initial radius for the clipping circle
        val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animation (the final radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(boxLayout, cx, cy, initialRadius, 0f)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                boxLayout.visibility = View.INVISIBLE
            }
        })

        // start the animation
        anim.start()
    }
}
