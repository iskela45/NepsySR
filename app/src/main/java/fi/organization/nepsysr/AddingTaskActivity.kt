package fi.organization.nepsysr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import fi.organization.nepsysr.alarm.AlarmHandler
import fi.organization.nepsysr.utilities.compressBitmap
import fi.organization.nepsysr.utilities.convertBitmap

class AddingTaskActivity : AppCompatActivity() {

    lateinit var editTitle: EditText
    lateinit var setTimer: EditText
    lateinit var editTopic: EditText
    lateinit var saveTask: Button

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_task)

        this.editTitle = findViewById(R.id.editTitle)
        this.setTimer = findViewById(R.id.setTimer)
        this.editTopic = findViewById(R.id.editTopic)
        this.saveTask = findViewById(R.id.saveTask)
        var taskImageView: ImageView = findViewById(R.id.imageView)

        var taskId = intent.getIntExtra("taskId", -1)
        var itIsUpdate = intent.getBooleanExtra("update", false)
        var contactUserId = intent.getIntExtra("contactUserId", -1)


        if (itIsUpdate) {
            editTitle.setText(intent.getStringExtra("title").toString())
            setTimer.setText(intent.getIntExtra("timer", 0).toString())
            editTopic.setText(intent.getStringExtra("topic").toString())
            this.findViewById<TextView>(R.id.tvHeading).setText("Päivitä tehtävää")
            this.findViewById<TextView>(R.id.saveTask).setText("Päivitä")
        }

        saveTask.setOnClickListener {
            var title = editTitle.text.toString()
            var timer = setTimer.text.toString()
            var topic = editTopic.text.toString()

            if (timer.isNotEmpty()) {
                val alarm = AlarmHandler(this)

                alarm.start(title, timer, topic)

                var requestCode = alarm.getRequestCode()
                var daysRemain = alarm.getDaysDifference()
                val drawable = taskImageView.drawable
                val bitmap = drawable.toBitmap()

                val data = Intent()
                data.putExtra("title", title)
                data.putExtra("timer", timer)
                data.putExtra("topic", topic)
                data.putExtra("img", convertBitmap(bitmap))
                data.putExtra("requestCode", requestCode)
                data.putExtra("daysRemain", daysRemain)
                data.putExtra("taskId", taskId)
                data.putExtra("contactUserId", contactUserId)
                if(itIsUpdate){
                    setResult(2000, data)
                } else {
                    setResult(RESULT_OK, data)
                }

                finish()
            } else {
                Toast.makeText(applicationContext,"Aseta ajastimeen tehtävän ilmoitusten aikaväli",Toast.LENGTH_LONG).show()
            }
        }

        // Check and ask for permissions, then start gallery activity.
        taskImageView.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    ActivityCompat.startActivityForResult(this as Activity, takePictureIntent, 1002, null)
                }

                else -> {
                    // You can directly ask for the permission.
                    ActivityCompat.requestPermissions(
                        this as Activity,
                        arrayOf(Manifest.permission.CAMERA),
                        1002
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bitmap = data?.extras?.get("data") as Bitmap?

        if(requestCode == 1002 && bitmap != null){
            val img : ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(compressBitmap(bitmap))
        }
    }
}