package com.timwp.weatherornotalarm

import java.util.*
import kotlin.collections.ArrayList

class util {
    companion object {
        fun timeString(timeInMillis: Long): String {
            val cal = Calendar.getInstance()
            cal.setTimeInMillis(timeInMillis)
            val hour = cal.get(Calendar.HOUR)
            val hourString = if (hour == 0) "12" else "$hour"
            val minute = cal.get(Calendar.MINUTE)
            val minuteString = if (minute < 10) "0$minute" else "$minute"
            val am_pm = if (cal.get(Calendar.AM_PM) == 1) "pm" else "am"
            return "$hourString:$minuteString $am_pm"
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
        data class TimeObject(
                val hour: Int,
                val minute: Int
        )
    }
}