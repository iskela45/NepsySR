package fi.organization.nepsysr.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import fi.organization.nepsysr.database.AppRoomDatabase
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

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.N)
    fun start(title : String, timer : String, topic: String) {

        val splitTime = time.getAlarmTime()
        val notificationId = rnd.getRandomNumber()
        this.penIntentRequestCode = rnd.getRandomNumber()

        val cal : Calendar = time.getExactAlarmTime(splitTime, timer)
        val alarmStartTime : Long = cal.timeInMillis
        this.days = time.getDaysDifference(cal)

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)
        intent.putExtra("days", days)
        intent.putExtra("requestCode", penIntentRequestCode)


        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(
            context,
            penIntentRequestCode,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarm : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Set alarm (type, milliseconds, intent)
        alarm.setExact(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent)
    }

    fun getRequestCode() : Int {
        return penIntentRequestCode
    }

    fun getDaysDifference() : Int {
        return days
    }

    fun updateSpecificTaskAlarm(id : Int, timer: Int, reqCode: Int, title: String, topic: String) {
        //var task = database.appDao().getTask(id)
        val alarmTime = time.getAlarmTime()
        thread {
            database.appDao().updateTimer(id, timer)
            intentHandler(timer,title, topic, reqCode, alarmTime)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun updateAlarm(newTime : String = "") {

        var splitTime : List<String>? = null

        splitTime = if (newTime.isEmpty()) {
            time.getAlarmTime()
        } else newTime.split(":")

       thread {
            val tasks = database.appDao().getAllTasksList()
            for (i in tasks) {
                intentHandler(i.daysRemain, i.title, i.topic, i.requestCode, splitTime)
                //requestCodes.add(i.requestCode)
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @RequiresApi(Build.VERSION_CODES.N)
    fun intentHandler(daysRemain : Int,
                      title: String,
                      topic: String,
                      requestCode: Int,
                      splitTime: List<String>
    ) {
        val notificationId = rnd.getRandomNumber()
        penIntentRequestCode = requestCode

        val cal : Calendar = time.getExactAlarmTime(splitTime, daysRemain.toString())
        val alarmStartTime : Long = cal.timeInMillis

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)
        intent.putExtra("days", daysRemain)
        intent.putExtra("requestCode", requestCode)

        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(
            context,
            penIntentRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarm : AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Set alarm (type, milliseconds, intent)
        alarm.setExact(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent)

    }

    @SuppressLint("UnspecifiedImmutableFlag")
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