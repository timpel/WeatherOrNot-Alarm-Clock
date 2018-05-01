package com.timwp.weatherornotalarm

import android.content.Context
import android.content.res.Resources
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

class util {
    companion object {
        fun timeString(timeInMillis: Long): SpannableString {
            val cal = Calendar.getInstance()
            cal.setTimeInMillis(timeInMillis)
            val hour = cal.get(Calendar.HOUR)
            val hourString = if (hour == 0) "12" else "$hour"
            val minute = cal.get(Calendar.MINUTE)
            val minuteString = if (minute < 10) "0$minute" else "$minute"
            val am_pm = if (cal.get(Calendar.AM_PM) == 1) "PM" else "AM"
            val timeString = "$hourString:$minuteString $am_pm"
            val spannableString: SpannableString = SpannableString(timeString)
            spannableString.setSpan(RelativeSizeSpan(1.25f), 0, spannableString.length - 3, 0)
            return spannableString
        }
        fun timeString(hour: Int, minute: Int): String {
            val am_pm: String = if (hour >= 12) "PM" else "AM"
            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
            return formattedHour.toString() + ":" + (if (minute < 10) "0" else "") + minute.toString() + " " + am_pm
        }
        fun timeObject(timeString: String): TimeObject {
            val splitString = timeString.split(":", " ")
            val hour = splitString[0].toInt()
            val minute = splitString[1].toInt()
            val formattedHour = if (splitString[2] == "AM") {
                hour % 12
            } else {
                if (hour == 12) 12 else hour + 12
            }
            return TimeObject(formattedHour, minute)
        }
        fun toDayArray(boolArr: BooleanArray): Array<String> {
            val daysOfTheWeek = arrayOf("SU",
                    "M",
                    "TU",
                    "W",
                    "TH",
                    "F",
                    "SA")
            val dayList: ArrayList<String> = arrayListOf()
            for ((i, value) in boolArr.withIndex()) {
                if (value) dayList.add(daysOfTheWeek[i])
            }
            return dayList.toTypedArray()
        }
        fun setCalendar(time: util.Companion.TimeObject): Calendar {
            val alarmCalendar = Calendar.getInstance()
            alarmCalendar.set(Calendar.HOUR_OF_DAY, time.hour)
            alarmCalendar.set(Calendar.MINUTE, time.minute)
            alarmCalendar.set(Calendar.SECOND, 0)
            val now = Calendar.getInstance().time
            if (alarmCalendar.time <= now) {
                alarmCalendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return alarmCalendar
        }
        data class TimeObject(
                val hour: Int,
                val minute: Int
        )
    }
}