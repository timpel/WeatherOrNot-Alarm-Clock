package com.timwp.weatherornotalarm

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.TextView
import com.timwp.weatherornotalarm.util.Companion.timeString
import kotlinx.android.synthetic.main.activity_set_alarm.*
import java.util.*
import kotlin.math.abs


class SetMultiAlarmActivity : AppCompatActivity() {

    private lateinit var defaultAlarmLabel: TextView
    private lateinit var weatherAlarmLabel: TextView
    private lateinit var toneLabel: TextView
    private lateinit var boxArray: Array<CheckedTextView>
    private lateinit var weatherCriteriaIcon: ImageView
    private lateinit var weatherCriteriaLabel: TextView
    private var defaultTime: util.Companion.TimeObject? = null
    private var weatherTime: util.Companion.TimeObject? = null
    private var defaultAlarm: Alarm? = null
    private lateinit var ringtonePath: Uri

    private lateinit var weatherCriteria: IWeatherCriteria

    private val SET_SINGLE_ALARM_REQUEST_CODE = 0
    private val CRITERIA_PICKER_RESPONSE_CODE = 1
    private val TONE_PICKER_REQUEST_CODE = 2
    private val CONDITION_PICKER_REQUEST_CODE = 3
    private val TEMP_PICKER_REQUEST_CODE = 4
    private val WIND_PICKER_REQUEST_CODE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_multi_alarm)
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
            defaultTime = util.Companion.TimeObject(hour, minute)
            defaultAlarmLabel.text = util.timeString(hour, minute)
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
            weatherTime = util.Companion.TimeObject(hour, minute)
            weatherAlarmLabel.text = util.timeString(hour, minute)
        } else {
            weatherAlarmLabel.text = getString(R.string.none)
            weatherTime = null
        }
    }

    private fun initializeRepeatBar() {
        val currentRepeats = intent.getBooleanArrayExtra("CURRENT_REPEATS")
        boxArray = arrayOf(findViewById(R.id.sunday),
                findViewById(R.id.monday),
                findViewById(R.id.tuesday),
                findViewById(R.id.wednesday),
                findViewById(R.id.thursday),
                findViewById(R.id.friday),
                findViewById(R.id.saturday))

        for ((index, value) in currentRepeats.withIndex()) {
            boxArray[index].isChecked = value
            boxArray[index].setTextColor(Color.DKGRAY)
        }
    }

    private fun initializeToneBar() {
        ringtonePath = intent.getParcelableExtra("CURRENT_RINGTONE") ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        toneLabel = findViewById(R.id.tone_label)
        toneLabel.text = RingtoneManager.getRingtone(this, ringtonePath).getTitle(this)
    }

    private fun initializeWeatherCriteria() {
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
                criteriaArray[7])
        setWeatherCriteriaIcon()
    }

    private fun setWeatherCriteriaIcon() {
        weatherCriteriaIcon.visibility = View.VISIBLE
        when {
            weatherCriteria.conditions == getString(R.string.sun) -> {
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
                weatherCriteriaIcon.visibility = View.INVISIBLE
            }
        }
    }

    fun onClickDayCheckedText(v: View) {
        val checkedText = v as CheckedTextView
        checkedText.isChecked = !checkedText.isChecked
        when (checkedText.isChecked) {
            true -> {
                checkedText.setTextColor(Color.WHITE)
            }
            else -> {
                checkedText.setTextColor(Color.DKGRAY)
            }
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
        setDefaultAlarm()
        setWeatherAlarm()
        back()
    }

    private fun setCalendar(time: util.Companion.TimeObject): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, time.hour)
        calendar.set(Calendar.MINUTE, time.minute)
        var second: Int = Calendar.getInstance().get(Calendar.SECOND) + 3
        if (second > 59) {
            second %= 60
            calendar.set(Calendar.MINUTE, timepicker.minute + 1)
            calendar.set(Calendar.SECOND, second)
        }
        return calendar
    }

    private fun repeatDays(): BooleanArray {
        return boxArray.map { it -> it.isChecked }.toBooleanArray()
    }

    private fun setDefaultAlarm() {
        if (defaultTime == null) Log.e("setDefaultAlarm", "Default alarm time null")
        else {
            val calendar = setCalendar(defaultTime!!)
            val alarmSettings = IAlarmSettings(
                    Alarm.ALARM_TYPE_DEFAULT,
                    abs((Calendar.getInstance().timeInMillis).toInt()),
                    calendar.timeInMillis,
                    defaultTime!!.hour,
                    defaultTime!!.minute,
                    "Vancouver, BC",
                    IWeatherCriteria("","","","","","","",""),
                    "false",
                    repeatDays(),
                    ringtonePath.toString()
            )

            defaultAlarm = Alarm(alarmSettings, this)
            defaultAlarm!!.set()
        }
    }

    private fun setWeatherAlarm() {
        if (weatherTime == null) Log.e("setDefaultAlarm", "Default alarm time null")
        else {
            val id = if (defaultAlarm != null) defaultAlarm!!.getID() - 1 else abs((Calendar.getInstance().timeInMillis).toInt())
            val calendar = setCalendar(weatherTime!!)
            val alarmSettings = IAlarmSettings(
                    Alarm.ALARM_TYPE_WEATHER,
                    id,
                    calendar.timeInMillis,
                    weatherTime!!.hour,
                    weatherTime!!.minute,
                    "Vancouver, BC",
                    weatherCriteria,
                    "false",
                    repeatDays(),
                    ringtonePath.toString()
            )

            val weatherAlarm = Alarm(alarmSettings, this)
            weatherAlarm.set()
        }
    }

    private fun back() {
        val launchIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(launchIntent)
    }

    fun onClickCancelButton(v: View) {
        back()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SET_SINGLE_ALARM_REQUEST_CODE -> {
                    when (data.getIntExtra("ALARM_TYPE", -1)) {
                        Alarm.ALARM_TYPE_DEFAULT -> {
                            if (data.getBooleanExtra("ALARM_SET", false)) {
                                defaultTime = util.Companion.TimeObject(data.getIntExtra("HOUR", -1),
                                        data.getIntExtra("MINUTE", -1))
                                defaultAlarmLabel.text = timeString(defaultTime!!.hour, defaultTime!!.minute)
                            } else {
                                defaultTime = null
                                defaultAlarmLabel.text = getString(R.string.none)
                            }
                        }
                        Alarm.ALARM_TYPE_WEATHER -> {
                            if (data.getBooleanExtra("ALARM_SET", false)) {
                                weatherTime = util.Companion.TimeObject(data.getIntExtra("HOUR", -1),
                                        data.getIntExtra("MINUTE", -1))
                                weatherAlarmLabel.text = timeString(weatherTime!!.hour, weatherTime!!.minute)
                            } else {
                                weatherTime = null
                                weatherAlarmLabel.text = getString(R.string.none)
                                weatherCriteriaIcon.visibility = View.INVISIBLE
                                resetWeatherCriteria()
                                weatherCriteriaLabel.text = ""
                            }
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
                    if (pickedCondition == null) Log.e("SetAlarmAcitivity: OnActivityResult", "Null response data from ConditionPickerActivity")
                    else {
                        weatherCriteriaLabel.text = pickedCondition
                        resetWeatherCriteria()
                        weatherCriteria.conditions = pickedCondition
                        setWeatherCriteriaIcon()
                    }
                }
                TEMP_PICKER_REQUEST_CODE -> {
                    val pickedTemperature = data.getStringArrayExtra("PICKED_TEMPERATURE")
                    if (pickedTemperature == null) {
                        Log.e("SetAlarmAcitivity: OnActivityResult", "Null response data from TemperaturePickerActivity")
                    } else {
                        val displayString = pickedTemperature.joinToString(" ")
                        weatherCriteriaLabel.text = displayString
                        resetWeatherCriteria()
                        weatherCriteria.tempOperator = pickedTemperature[0]
                        weatherCriteria.temp = pickedTemperature[1]
                        weatherCriteria.tempUnit = pickedTemperature[2].drop(1)
                        setWeatherCriteriaIcon()
                    }
                }
                WIND_PICKER_REQUEST_CODE -> {
                    val picked = data.getStringArrayExtra("PICKED_WIND")
                    if (picked == null) {
                        Log.e("SetAlarmAcitivity: OnActivityResult", "Null response data from TemperaturePickerActivity")
                    } else {
                        val displayString = picked.joinToString(" ").replace("-", "")
                        weatherCriteriaLabel.text = displayString
                        resetWeatherCriteria()
                        weatherCriteria.windOperator = picked[0]
                        weatherCriteria.windSpeed = picked[1]
                        weatherCriteria.windUnit = picked[2]
                        weatherCriteria.windDirection = picked[3]
                        setWeatherCriteriaIcon()
                    }
                }
            }
        }
    }
}
