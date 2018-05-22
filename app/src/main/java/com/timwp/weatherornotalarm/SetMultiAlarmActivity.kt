package com.timwp.weatherornotalarm

import android.app.Activity
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import com.timwp.weatherornotalarm.Util.Companion.timeString
import java.util.*
import kotlin.math.abs


class SetMultiAlarmActivity : AppCompatActivity() {

    private lateinit var defaultAlarmLabel: TextView
    private lateinit var weatherAlarmLabel: TextView
    private lateinit var toneLabel: TextView
    private lateinit var repeatDayLayout: ConstraintLayout
    private lateinit var repeatSwitch: Switch
    private lateinit var boxArray: Array<CheckedTextView>
    private lateinit var weatherCriteriaIcon: ImageView
    private lateinit var weatherCriteriaLabel: TextView
    private lateinit var weatherCriteriaBar: ConstraintLayout
    private var defaultTime: Util.Companion.TimeObject? = null
    private var weatherTime: Util.Companion.TimeObject? = null
    private lateinit var ringtonePath: Uri
    private var defaultColor: Int = 0

    private lateinit var weatherCriteria: IWeatherCriteria

    private val SET_SINGLE_ALARM_REQUEST_CODE = 0
    private val CRITERIA_PICKER_RESPONSE_CODE = 1
    private val TONE_PICKER_REQUEST_CODE = 2
    private val CONDITION_PICKER_REQUEST_CODE = 3
    private val TEMP_PICKER_REQUEST_CODE = 4
    private val WIND_PICKER_REQUEST_CODE = 5
    private val NO_REPEAT_ARRAY = BooleanArray(7, {false})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_multi_alarm)
        defaultColor = findViewById<TextView>(R.id.default_alarm_label).currentTextColor
        initializeDefaultAlarm()
        initializeWeatherAlarm()
        initializeRepeatBar()
        initializeToneBar()
        initializeWeatherCriteria()
    }

    private fun initializeDefaultAlarm() {
        defaultAlarmLabel = findViewById(R.id.default_alarm_label)

        val currentlySet = intent.getBooleanExtra("DEFAULT_CURRENTLY_SET", false)

        if (currentlySet) {
            val hour = intent.getIntExtra("DEFAULT_HOUR", -1)
            val minute = intent.getIntExtra("DEFAULT_MINUTE", -1)
            defaultTime = Util.Companion.TimeObject(hour, minute)
            defaultAlarmLabel.text = Util.timeString(hour, minute)
        } else {
            defaultAlarmLabel.text = getString(R.string.none)
            defaultTime = null
        }

    }

    private fun initializeWeatherAlarm() {
        weatherAlarmLabel = findViewById(R.id.weather_alarm_label)

        val currentlySet = intent.getBooleanExtra("WEATHER_CURRENTLY_SET", false)

        if (currentlySet) {
            val hour = intent.getIntExtra("WEATHER_HOUR", -1)
            val minute = intent.getIntExtra("WEATHER_MINUTE", -1)
            weatherTime = Util.Companion.TimeObject(hour, minute)
            weatherAlarmLabel.text = Util.timeString(hour, minute)
        } else {
            weatherAlarmLabel.text = getString(R.string.none)
            weatherTime = null
        }
    }

    private fun initializeRepeatBar() {
        repeatDayLayout = findViewById(R.id.day_layout)
        repeatSwitch = findViewById(R.id.repeat_switch)
        val currentRepeats = intent.getBooleanArrayExtra("CURRENT_REPEATS")
        repeatSwitch.isChecked = currentRepeats.contains(true)
        boxArray = arrayOf(findViewById(R.id.sunday),
                findViewById(R.id.monday),
                findViewById(R.id.tuesday),
                findViewById(R.id.wednesday),
                findViewById(R.id.thursday),
                findViewById(R.id.friday),
                findViewById(R.id.saturday))

        for ((index, value) in currentRepeats.withIndex()) {
            boxArray[index].isChecked = value
            boxArray[index].setTextColor(if (value) defaultColor else Color.DKGRAY)
        }

        repeatSwitch.setOnCheckedChangeListener { compoundButton, b ->
            val checked = repeatSwitch.isChecked
            repeatDayLayout.visibility = if (checked) View.VISIBLE else View.GONE
            if (!boxArray.map {it -> it.isChecked}.contains(true)) {
                for ((index, value) in currentRepeats.withIndex()) {
                    boxArray[index].isChecked = checked
                    boxArray[index].setTextColor(if (checked) defaultColor else Color.DKGRAY)
                }
            }
            Log.e("repeatSwitch", "Repeats switched " + if (checked) "on" else "off")
        }
        repeatDayLayout.visibility = if (repeatSwitch.isChecked) View.VISIBLE else View.GONE
    }

    private fun initializeToneBar() {
        val currentRingtone = intent.getStringExtra("CURRENT_RINGTONE")
        ringtonePath = if (currentRingtone == null) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            Uri.parse(currentRingtone)
        }
        toneLabel = findViewById(R.id.tone_label)
        toneLabel.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)
    }

    private fun initializeWeatherCriteria() {
        weatherCriteriaBar = findViewById(R.id.weather_criteria_bar)
        weatherCriteriaLabel = findViewById(R.id.weather_criteria_label)
        weatherCriteriaIcon = findViewById(R.id.weather_criteria_icon)
        val criteriaArray = intent.getStringArrayExtra("CURRENT_WEATHER_CRITERIA")
        weatherCriteria = IWeatherCriteria(criteriaArray[0],
                criteriaArray[1],
                criteriaArray[2],
                criteriaArray[3],
                criteriaArray[4],
                criteriaArray[5],
                criteriaArray[6],
                criteriaArray[7].replace("-", ""))
        setWeatherCriteriaView()
        weatherCriteriaLabel.text = criteriaArray.joinToString(" ").trim()
    }

    private fun setWeatherCriteriaView() {
        weatherCriteriaBar.visibility = if (weatherTime == null) View.INVISIBLE else {
            weatherCriteriaIcon.clearColorFilter()
            when {
                weatherCriteria.conditions == getString(R.string.clear) -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.sun_icon)
                }
                weatherCriteria.conditions == getString(R.string.cloud) -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.cloud_icon)
                }
                weatherCriteria.conditions == getString(R.string.rain) -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.rain_icon)
                }
                weatherCriteria.conditions == getString(R.string.snow) -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.snow_icon)
                }
                weatherCriteria.tempOperator == "Above" -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.temp_above_icon)
                }
                weatherCriteria.tempOperator == "Below" -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.temp_below_icon)
                }
                weatherCriteria.windOperator != "" -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.wind_icon)
                }
                else -> {
                    weatherCriteriaIcon.setImageResource(R.drawable.ic_add_circle_outline_white_24px)
                    weatherCriteriaIcon.setColorFilter(Color.parseColor("#66BB66"))
                    weatherCriteriaLabel.text = "Select Criteria"
                }
            }
            View.VISIBLE
        }
    }

    fun onClickDayCheckedText(v: View) {
        val checkedText = v as CheckedTextView
        val onlyOneChecked = !boxArray.map {it -> it.isChecked}.contains(true)
        checkedText.toggle()
        when (checkedText.isChecked) {
            true -> {
                checkedText.setTextColor(defaultColor)
            }
            else -> {
                checkedText.setTextColor(Color.DKGRAY)
            }
        }
        if (!boxArray.map {it -> it.isChecked}.contains(true)) {
            repeatSwitch.isChecked = false
            repeatDayLayout.visibility = View.VISIBLE
        }
        else if (onlyOneChecked) {
            repeatSwitch.isChecked = true
        }
    }

    fun onClickDefault(v: View) {
        val launchIntent = Intent(applicationContext, SetSingleAlarmActivity::class.java)
        launchIntent.putExtra("ALARM_TYPE",Alarm.ALARM_TYPE_DEFAULT)

        if (defaultTime == null) {
            launchIntent.putExtra("CURRENTLY_PICKED", false)
        } else {
            launchIntent.putExtra("CURRENTLY_PICKED", true)
            launchIntent.putExtra("HOUR", defaultTime!!.hour)
            launchIntent.putExtra("MINUTE", defaultTime!!.minute)
        }
        startActivityForResult(launchIntent, SET_SINGLE_ALARM_REQUEST_CODE)
    }

    fun onClickWeather(v: View) {
        val launchIntent = Intent(applicationContext, SetSingleAlarmActivity::class.java)
        launchIntent.putExtra("ALARM_TYPE",Alarm.ALARM_TYPE_WEATHER)

        if (weatherTime == null) {
            launchIntent.putExtra("CURRENTLY_PICKED", false)
        } else {
            launchIntent.putExtra("CURRENTLY_PICKED", true)
            launchIntent.putExtra("HOUR", weatherTime!!.hour)
            launchIntent.putExtra("MINUTE", weatherTime!!.minute)
        }
        startActivityForResult(launchIntent, SET_SINGLE_ALARM_REQUEST_CODE)
    }

    fun onClickToneBar(v: View) {
        val tonePickerIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)

        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtonePath)
        tonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, false)
        startActivityForResult(tonePickerIntent, TONE_PICKER_REQUEST_CODE)
    }

    fun onClickCriteria(v: View) {
        val launchIntent = Intent(applicationContext, CriteriaPickerActivity::class.java)
        startActivityForResult(launchIntent, CRITERIA_PICKER_RESPONSE_CODE)
    }

    private fun resetWeatherCriteria() {
        weatherCriteria = IWeatherCriteria("",
                "",
                "",
                "",
                "",
                "",
                "",
                "")
    }

    fun onClickSetAlarmButton(v: View) {
        if (defaultTime == null && weatherTime == null) {
            val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            alertBuilder.setMessage("Choose a time for at least one alarm")
                    .setPositiveButton("OK", { dialogInterface, i ->

                    })
            alertBuilder.create().show()
            return
        }

        val currentAlarmPairID = intent.getIntExtra("ALARM_PAIR_ID", -1)
        val alarmPairID = if (currentAlarmPairID != -1) currentAlarmPairID
        else abs((Calendar.getInstance().timeInMillis).toInt())
        val defaultAlarm = configureDefaultAlarm(alarmPairID)
        val weatherAlarm = configureWeatherAlarm(alarmPairID)

        if (weatherAlarm != null && weatherAlarm.getWeatherCriteriaAsStringArray().contentEquals(Array(8, { it -> ""}))) {
            val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            alertBuilder.setMessage("Choose WeatherAlarm Criteria, or use only the Default Alarm if you want this alarm to ring regardless of weather.")
                    .setPositiveButton("OK", { dialogInterface, i ->

                    })
            alertBuilder.create().show()
            return
        }

        if (defaultAlarm != null && weatherAlarm != null && defaultAlarm < weatherAlarm) {
            val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            alertBuilder.setMessage("Default alarm time is sooner than WeatherAlarm time. Set default for a day later?")
                    .setPositiveButton("Yes", { dialogInterface, i ->
                        defaultAlarm.addADay()
                        setAlarmPair(alarmPairID, defaultAlarm, weatherAlarm)
                        back()
                    }).setNegativeButton("Cancel", { dialogInterface, i ->
                        return@setNegativeButton
                    })
            alertBuilder.create().show()
        } else {
            setAlarmPair(alarmPairID, defaultAlarm, weatherAlarm)
            back()
        }
    }

    private fun setAlarmPair(alarmPairID: Int, defaultAlarm: Alarm?, weatherAlarm: Alarm?) {
        val alarmPair = AlarmPair(alarmPairID, defaultAlarm, weatherAlarm, true, applicationContext)
        AlarmPairManager.getInstance(applicationContext).addAlarmPair(alarmPair)
        alarmPair.persist()
        defaultAlarm?.set()
        weatherAlarm?.set()
    }

    private fun repeatDays(): BooleanArray {
        return if (repeatSwitch.isChecked) boxArray.map { it -> it.isChecked }.toBooleanArray() else NO_REPEAT_ARRAY
    }

    private fun configureDefaultAlarm(alarmPairID: Int): Alarm? {
        if (defaultTime == null) {
            Log.e("setDefaultAlarm", "Default alarm time null")
            return null
        }
        else {
            val calendar = Util.setCalendar(defaultTime!!)
            val alarmSettings = IAlarmSettings(
                    Alarm.ALARM_TYPE_DEFAULT,
                    abs((Calendar.getInstance().timeInMillis).toInt()),
                    alarmPairID,
                    calendar.timeInMillis,
                    defaultTime!!.hour,
                    defaultTime!!.minute,
                    "Vancouver, BC",
                    IWeatherCriteria("","","","","","","",""),
                    "false",
                    repeatDays(),
                    ringtonePath.toString()
            )
            return Alarm(alarmSettings, this)
        }
    }

    private fun configureWeatherAlarm(alarmPairID: Int): Alarm? {
        if (weatherTime == null) {
            Log.e("configureWeatherAlarm", "Weather alarm time null")
            return null
        }
        else {
            val id = abs((Calendar.getInstance().timeInMillis).toInt()) + 1
            val calendar = Util.setCalendar(weatherTime!!)
            val alarmSettings = IAlarmSettings(
                    Alarm.ALARM_TYPE_WEATHER,
                    id,
                    alarmPairID,
                    calendar.timeInMillis,
                    weatherTime!!.hour,
                    weatherTime!!.minute,
                    "Vancouver, BC",
                    weatherCriteria,
                    "false",
                    repeatDays(),
                    ringtonePath.toString()
            )
            return Alarm(alarmSettings, this)
        }
    }

    private fun back() {
        val launchIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(launchIntent)
    }

    fun onClickDeleteButton(v: View) {
        val currentAlarmPairID = intent.getIntExtra("ALARM_PAIR_ID", -1)
        if (currentAlarmPairID != -1) {
            val alarmPairManager = AlarmPairManager.getInstance(applicationContext)
            alarmPairManager.removeAlarmPair(currentAlarmPairID)
        }
        back()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SET_SINGLE_ALARM_REQUEST_CODE -> {
                    when (data.getIntExtra("ALARM_TYPE", -1)) {
                        Alarm.ALARM_TYPE_DEFAULT -> {
                            if (data.getBooleanExtra("ALARM_SET", false)) {
                                defaultTime = Util.Companion.TimeObject(data.getIntExtra("HOUR", -1),
                                        data.getIntExtra("MINUTE", -1))
                                defaultAlarmLabel.text = timeString(defaultTime!!.hour, defaultTime!!.minute)
                            } else {
                                defaultTime = null
                                defaultAlarmLabel.text = getString(R.string.none)
                            }
                        }
                        Alarm.ALARM_TYPE_WEATHER -> {
                            if (data.getBooleanExtra("ALARM_SET", false)) {
                                weatherTime = Util.Companion.TimeObject(data.getIntExtra("HOUR", -1),
                                        data.getIntExtra("MINUTE", -1))
                                weatherAlarmLabel.text = timeString(weatherTime!!.hour, weatherTime!!.minute)
                            } else {
                                weatherTime = null
                                weatherAlarmLabel.text = getString(R.string.none)
                                resetWeatherCriteria()
                            }
                            setWeatherCriteriaView()
                        }
                    }
                }
                TONE_PICKER_REQUEST_CODE -> {
                    Log.e("onActivityResult", "TonePicker responded")
                    val pickedURI = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                    if (pickedURI != null) {
                        ringtonePath = pickedURI
                        toneLabel.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)
                    }
                }
                CRITERIA_PICKER_RESPONSE_CODE -> {
                    when (data.getStringExtra("CRITERIA_BROAD_CATEGORY")) {
                        getString(R.string.conditions) -> {
                            val launchIntent = Intent(applicationContext, ConditionPickerActivity::class.java)
                            startActivityForResult(launchIntent, CONDITION_PICKER_REQUEST_CODE)
                        }
                        getString(R.string.temperature) -> {
                            val launchIntent = Intent(applicationContext, TemperaturePickerActivity::class.java)
                            val currentTempCriteria = arrayOf(weatherCriteria.tempOperator,
                                    weatherCriteria.temp, weatherCriteria.tempUnit)
                            launchIntent.putExtra("CURRENT_TEMP_CRITERIA",
                                    if (currentTempCriteria.contentEquals(arrayOf("","",""))) null
                                    else currentTempCriteria)
                            startActivityForResult(launchIntent, TEMP_PICKER_REQUEST_CODE)
                        }
                        getString(R.string.wind) -> {
                            val launchIntent = Intent(applicationContext, WindPickerActivity::class.java)
                            val currentWindCriteria = arrayOf(weatherCriteria.windOperator,
                                    weatherCriteria.windSpeed, weatherCriteria.windUnit, weatherCriteria.windDirection)
                            launchIntent.putExtra("CURRENT_WIND_CRITERIA",
                                    if (currentWindCriteria.contentEquals(arrayOf("","","",""))) null
                                    else currentWindCriteria)
                            startActivityForResult(launchIntent, WIND_PICKER_REQUEST_CODE)
                        }
                    }
                }
                CONDITION_PICKER_REQUEST_CODE -> {
                    val pickedCondition = data.getStringExtra("PICKED_CONDITION")
                    if (pickedCondition == null) Log.e("OnActivityResult", "Null response data from ConditionPickerActivity")
                    else {
                        weatherCriteriaLabel.text = pickedCondition
                        resetWeatherCriteria()
                        weatherCriteria.conditions = pickedCondition
                        setWeatherCriteriaView()
                    }
                }
                TEMP_PICKER_REQUEST_CODE -> {
                    val pickedTemperature = data.getStringArrayExtra("PICKED_TEMPERATURE")
                    if (pickedTemperature == null) {
                        Log.e("OnActivityResult", "Null response data from TemperaturePickerActivity")
                    } else {
                        val displayString = pickedTemperature.joinToString(" ")
                        weatherCriteriaLabel.text = displayString
                        resetWeatherCriteria()
                        weatherCriteria.tempOperator = pickedTemperature[0]
                        weatherCriteria.temp = pickedTemperature[1]
                        weatherCriteria.tempUnit = pickedTemperature[2].drop(1)
                        setWeatherCriteriaView()
                    }
                }
                WIND_PICKER_REQUEST_CODE -> {
                    val picked = data.getStringArrayExtra("PICKED_WIND")
                    if (picked == null) {
                        Log.e("OnActivityResult", "Null response data from TemperaturePickerActivity")
                    } else {
                        val displayString = picked.joinToString(" ").replace("-", "")
                        weatherCriteriaLabel.text = displayString
                        resetWeatherCriteria()
                        weatherCriteria.windOperator = picked[0]
                        weatherCriteria.windSpeed = picked[1]
                        weatherCriteria.windUnit = picked[2]
                        weatherCriteria.windDirection = picked[3]
                        setWeatherCriteriaView()
                    }
                }
            }
        }
    }
}
