package com.timwp.weatherornotalarm

import android.content.Intent
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView

class AlarmPairAdapter(private var alarmPairs: ArrayList<AlarmPair>) : RecyclerView.Adapter<AlarmPairAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = alarmPairs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val thisAlarmPair = alarmPairs[position]
        val defaultAlarm = thisAlarmPair.getDefaultAlarm()
        val weatherAlarm = thisAlarmPair.getWeatherAlarm()

        if (defaultAlarm != null) {
            holder.defaultAlarmLabel.text = util.timeString(defaultAlarm.getAlarmTime())
            holder.defaultAlarmLabel.visibility = View.VISIBLE
            holder.noDefaultLabel.visibility = View.INVISIBLE
        } else {
            holder.defaultAlarmLabel.visibility = View.INVISIBLE
            holder.noDefaultLabel.visibility = View.VISIBLE
            holder.defaultTitle.visibility = View.INVISIBLE
        }

        if (weatherAlarm != null) {
            holder.weatherAlarmLabel.text = util.timeString(weatherAlarm.getAlarmTime())
            holder.weatherAlarmLabel.visibility = View.VISIBLE
            holder.noWeatherLabel.visibility = View.INVISIBLE


            val weatherCriteria = weatherAlarm.getWeatherCriteria()
            val criteriaStringArray = arrayOf(
                    weatherCriteria.tempOperator,
                    weatherCriteria.temp,
                    if (weatherCriteria.tempUnit != "") '\u00B0' + weatherCriteria.tempUnit else "",
                    weatherCriteria.windOperator,
                    weatherCriteria.windSpeed, weatherCriteria.windUnit, weatherCriteria.windDirection)
            val criteriaString = criteriaStringArray.joinToString(" ").replace("-", "").trim()

            when {
                weatherCriteria.conditions == "Sunny" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.sun_icon)
                }
                weatherCriteria.conditions == "Cloudy" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.cloud_icon)
                }
                weatherCriteria.conditions == "Raining" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.rain_icon)
                }
                weatherCriteria.conditions == "Snowing" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.snow_icon)
                }
                weatherCriteria.tempOperator == "Above" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.temp_above_icon)
                }
                weatherCriteria.tempOperator == "Below" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.temp_below_icon)
                }
                weatherCriteria.windOperator != "" -> {
                    holder.weatherCriteriaIcon.setImageResource(R.drawable.wind_icon)
                }
                else -> {
                    holder.weatherCriteriaIcon.visibility = View.INVISIBLE
                }
            }
            holder.weatherCriteriaLabel.text = criteriaString
            holder.weatherCriteriaContainer.visibility = View.VISIBLE
        }
        else {
            holder.weatherAlarmLabel.visibility = View.INVISIBLE
            holder.noWeatherLabel.visibility = View.VISIBLE
            holder.weatherCriteriaContainer.visibility = View.INVISIBLE
        }

        val nonNullAlarm: Alarm = defaultAlarm ?: weatherAlarm!!
        val dayArray = util.toDayArray(nonNullAlarm.getSettings().repeat)
        val dayString = if (dayArray.isNotEmpty()) dayArray.joinToString(", ") else "Non-repeating"
        holder.repeatLabel.text = dayString

        holder.activationSwitch.isChecked = thisAlarmPair.isActive()

        holder.activationSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (holder.activationSwitch.isChecked) thisAlarmPair.activate()
            else thisAlarmPair.deactivate()
            setFontColors(holder, thisAlarmPair.isActive())
            Log.e("Activation Switch onCheckedChange", "Alarm pair active = " + thisAlarmPair.isActive())
        }
        setFontColors(holder, thisAlarmPair.isActive())

        holder.settingsButton.setOnClickListener { it ->
            val launchIntent = Intent(holder.context, SetMultiAlarmActivity::class.java)
            launchIntent.putExtra("ALARM_PAIR_ID", thisAlarmPair.getID())
            launchIntent.putExtra("DEFAULT_CURRENTLY_SET", defaultAlarm != null)
            launchIntent.putExtra("WEATHER_CURRENTLY_SET", weatherAlarm != null)
            launchIntent.putExtra("CURRENT_RINGTONE", nonNullAlarm.getSettings().ringtoneURIString)
            launchIntent.putExtra("DEFAULT_HOUR", defaultAlarm?.getSettings()?.hour)
            launchIntent.putExtra("DEFAULT_MINUTE", defaultAlarm?.getSettings()?.minute)
            launchIntent.putExtra("WEATHER_HOUR", weatherAlarm?.getSettings()?.hour)
            launchIntent.putExtra("WEATHER_MINUTE", weatherAlarm?.getSettings()?.minute)
            launchIntent.putExtra("CURRENT_REPEATS", nonNullAlarm.getSettings().repeat)
            launchIntent.putExtra("CURRENT_WEATHER_CRITERIA",
                    if (weatherAlarm == null) Array(8, { _ -> "" })
                    else thisAlarmPair.getWeatherAlarm()!!.getWeatherCriteriaAsStringArray())
            holder.context.startActivity(launchIntent)
            Log.e("Logging", "in SetMultiAlarm")
        }
    }

    fun setFontColors(holder: ViewHolder, isActive: Boolean) {
        if (isActive) holder.allLabels.forEach { it.setTextColor(holder.defaultColor) }
        else holder.allLabels.forEach { it.setTextColor(Color.parseColor("#555555")) }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val defaultAlarmLabel: TextView = itemView.findViewById(R.id.default_time_label)
        val weatherAlarmLabel: TextView = itemView.findViewById(R.id.weather_time_label)
        val weatherCriteriaContainer: ConstraintLayout = itemView.findViewById(R.id.weather_criteria_container)
        val weatherCriteriaIcon: ImageView = itemView.findViewById(R.id.weather_criteria_icon)
        val weatherCriteriaLabel: TextView = itemView.findViewById(R.id.weather_criteria_label)
        val noDefaultLabel: TextView = itemView.findViewById(R.id.no_default_label)
        val noWeatherLabel: TextView = itemView.findViewById(R.id.no_weather_label)
        val defaultTitle: TextView = itemView.findViewById(R.id.default_title)
        val repeatLabel: TextView = itemView.findViewById(R.id.repeat_label)
        val settingsButton: ImageView = itemView.findViewById(R.id.settings_button)
        val activationSwitch: Switch = itemView.findViewById(R.id.activation_switch)
        val allLabels = arrayListOf(defaultAlarmLabel, weatherAlarmLabel, weatherCriteriaLabel,
                noDefaultLabel, noWeatherLabel, defaultTitle, repeatLabel)
        val defaultColor = defaultAlarmLabel.currentTextColor
    }
}