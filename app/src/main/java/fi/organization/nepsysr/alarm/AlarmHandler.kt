package fi.organization.nepsysr.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi

class AlarmHandler {

    private var days: Int = 0
    private var penIntentRequestCode: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
    fun start(title : String, timer : String, topic: String, context : Context) {

        // Initializes objects
        val time = TimeHandler()
        val rnd = RandomGenerate()

        val splittedTime = time.getAlarmTime(context)
        var notificationId = rnd.getRandomNumber()
        this.penIntentRequestCode = rnd.getRandomNumber()

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)

        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(context, penIntentRequestCode,
            intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarm : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        var cal : Calendar = time.getExactAlarmTime(splittedTime, timer)
        var alarmStartTime : Long = cal.timeInMillis
        this.days = time.getDaysDifference(cal)

        // Set alarm (type, milliseconds, intent)
        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent)
    }

    fun getRequestCode() : Int {
        return penIntentRequestCode
    }

    fun getDaysDifference() : Int {
        return days
    }
}