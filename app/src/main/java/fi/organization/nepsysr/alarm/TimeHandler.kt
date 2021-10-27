package fi.organization.nepsysr.alarm

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class TimeHandler {

    private var days: Int? = null

    fun getAlarmTime(context : Context): List<String> {

        // Retrieves the user chosen alarm time from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val alarmSp = sp.getString("alarmTime", "")

        return alarmSp!!.split(":")
    }

    // Includes day
    @RequiresApi(Build.VERSION_CODES.N)
    fun getExactAlarmTime(splittedTime : List<String>, timer : String) : Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, splittedTime[0].toInt())
        cal.set(Calendar.MINUTE, splittedTime[1].toInt())
        cal.set(Calendar.SECOND, 0)

        // checks alarm's time, if it's before current time, add one day
        if(cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DATE, 1)
        }
        cal.add(Calendar.DATE, Integer.parseInt(timer))

        return cal
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDaysDifference(calDate : Calendar) : Int {

        // Calculates the difference between the current date and the days given by the user
        val today = Calendar.getInstance()
        val difference: Long =  calDate.timeInMillis - today.timeInMillis
        this.days = (difference / (1000 * 60 * 60 * 24)).toInt()

        return days as Int
    }
}
