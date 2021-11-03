package fi.organization.nepsysr.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import fi.organization.nepsysr.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.concurrent.thread


class AlarmHandler(val context: Context) {

    private val applicationScope = CoroutineScope(SupervisorJob())
    val database = AppRoomDatabase.getDatabase(context, applicationScope)
    private var days: Int = 0
    private var penIntentRequestCode: Int = 0
    private val time = TimeHandler(context)
    private val rnd = RandomGenerate()

    @RequiresApi(Build.VERSION_CODES.N)
    fun start(title : String, timer : String, topic: String) {

        val splittedTime = time.getAlarmTime()
        var notificationId = rnd.getRandomNumber()
        this.penIntentRequestCode = rnd.getRandomNumber()

        var cal : Calendar = time.getExactAlarmTime(splittedTime, timer)
        var alarmStartTime : Long = cal.timeInMillis
        this.days = time.getDaysDifference(cal)

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)
        intent.putExtra("days", days)

        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(context, penIntentRequestCode,
            intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarm : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager



        // Set alarm (type, milliseconds, intent)
        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent)
    }

    fun getRequestCode() : Int {
        return penIntentRequestCode
    }

    fun getDaysDifference() : Int {
        return days
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateAlarm(newTime : String = "") {

        var splittedTime : List<String>? = null

        splittedTime = if (newTime.isEmpty()) {
            time.getAlarmTime()
        } else newTime!!.split(":")

       thread {
            var tasks = database.appDao().getAllTasksList()
            for (i in tasks) {
                intentHandler(i.daysRemain, i.title, i.topic, i.requestCode, splittedTime)
                //requestCodes.add(i.requestCode)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun intentHandler(daysRemain : Int, title: String, topic: String, requestCode: Int, splittedTime: List<String>) {
        val notificationId = rnd.getRandomNumber()
        penIntentRequestCode = requestCode

        var cal : Calendar = time.getExactAlarmTimeForUpdate(splittedTime, daysRemain.toString())
        var alarmStartTime : Long = cal.timeInMillis

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)
        intent.putExtra("days", daysRemain)

        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(context, penIntentRequestCode,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarm : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Set alarm (type, milliseconds, intent)
        alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun timeObserve() {

        val intent = Intent(context, TimeReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            100, intent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        Log.d("no", calendar.time.toString())
        if(calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        val alarm = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarm.cancel(pendingIntent)
        alarm.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}