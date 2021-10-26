package fi.organization.nepsysr

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import fi.organization.nepsysr.receivers.AlarmReceiver
import java.util.*
import kotlin.properties.Delegates

class AddingTaskActivity : AppCompatActivity() {

    lateinit var editTitle: EditText
    lateinit var setTimer: EditText
    lateinit var editTopic: EditText
    lateinit var addImage: EditText
    lateinit var saveTask: Button
    private var days: Int? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_task)

        this.editTitle = findViewById(R.id.editTitle)
        this.setTimer = findViewById(R.id.setTimer)
        this.editTopic = findViewById(R.id.editTopic)
        this.addImage = findViewById(R.id.addImage)
        this.saveTask = findViewById(R.id.saveTask)

        saveTask.setOnClickListener {
            val title = editTitle.text.toString()
            val timer = setTimer.text.toString()
            val topic = editTopic.text.toString()
            val img = addImage.text.toString()

            startAlarm(title, timer, topic)

            val data = Intent()
            data.putExtra("title", title)
            data.putExtra("timer", timer)
            data.putExtra("topic", topic)
            data.putExtra("img", img)
            if (days != null) {
                data.putExtra("days", days)
            }

            setResult(RESULT_OK, data)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startAlarm(title : String, timer : String, topic: String) {

        val splittedTime = getAlarmTime()
        var notificationId = getRandomNumber()
        val penIntentRequestCode = getRandomNumber()

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)

        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(this, penIntentRequestCode,
            intent,PendingIntent.FLAG_CANCEL_CURRENT)
        val alarm : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        var cal : Calendar = getExactAlarmTime(splittedTime, timer)
        var alarmStartTime : Long = cal.timeInMillis
        getDaysDifference(cal)

        // Set alarm (type, milliseconds, intent)
        alarm.set(RTC_WAKEUP, alarmStartTime, alarmIntent)
    }

    private fun getAlarmTime() : List<String> {

        // Retrieves the user chosen alarm time from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        var alarmSp = sp.getString("alarmTime", "")
        var splittedTime = alarmSp!!.split(":")

        return splittedTime
    }

    fun getRandomNumber(): Int {
        return (0..999999).random()
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
            cal.add(Calendar.DATE, 1);
        }
        cal.add(Calendar.DATE, Integer.parseInt(timer))

        return cal
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDaysDifference(calDate : Calendar) {

        // Calculates the difference between the current date and the days given by the user
        val today = Calendar.getInstance()
        val difference: Long =  calDate.getTimeInMillis() - today.getTimeInMillis()
        this.days = (difference / (1000 * 60 * 60 * 24)).toInt()

        Log.d("TAGI","$days")
    }
}