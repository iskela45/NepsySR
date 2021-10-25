package fi.organization.nepsysr

import android.app.Activity
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import fi.organization.nepsysr.receivers.AlarmReceiver
import java.util.*

class AddingTaskActivity : AppCompatActivity() {

    lateinit var editTitle: EditText
    lateinit var setTimer: EditText
    lateinit var editTopic: EditText
    lateinit var addImage: EditText
    lateinit var saveTask: Button

    private var notificationId = 1

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
            setResult(RESULT_OK, data)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startAlarm(title : String, timer : String, topic: String) {

        // Retrieves the user chosen alarm time from shared preferences
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        var alarmSp = sp.getString("alarmTime", "")
        var splittedTime = alarmSp!!.split(":")

        val intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("title", title)
        intent.putExtra("topic", topic)

        // getBroadcast(context, requestCode, intent, flags)
        val alarmIntent : PendingIntent = PendingIntent.getBroadcast(this, 0,
            intent,PendingIntent.FLAG_CANCEL_CURRENT)
        val alarm : AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, splittedTime[0].toInt())
        cal.set(Calendar.MINUTE, splittedTime[1].toInt())
        cal.set(Calendar.SECOND, 0)

        var alarmStartTime : Long = cal.timeInMillis

        val calDate = Calendar.getInstance()
        //val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        calDate.add(Calendar.DATE, Integer.parseInt(timer))
        //val formattedDate: String = df.format(calDate)

        val today = Calendar.getInstance()

        // Calculates the difference between the current date and the days given by the user
        val difference: Long =  calDate.getTimeInMillis() - today.getTimeInMillis()
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()

        Log.d("TAGI","$days")

        // Set alarm (type, milliseconds, intent)
        alarm.set(RTC_WAKEUP, alarmStartTime, alarmIntent)
    }
}