package fi.organization.nepsysr

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import fi.organization.nepsysr.alarm.AlarmHandler
import fi.organization.nepsysr.alarm.AlarmReceiver

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

            val alarm = AlarmHandler()

            alarm.start(title, timer, topic, this)

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
}