package fi.organization.nepsysr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import fi.organization.nepsysr.alarm.AlarmHandler
import fi.organization.nepsysr.utilities.compressBitmap
import fi.organization.nepsysr.utilities.convertBitmap

class AddingTaskActivity : AppCompatActivity() {

    private lateinit var editTitle: EditText
    private lateinit var setTimer: EditText
    private lateinit var editTopic: EditText
    private lateinit var saveTask: Button
    private lateinit var taskImageView: ImageView

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_task)

        this.editTitle = findViewById(R.id.editTitle)
        this.setTimer = findViewById(R.id.setTimer)
        this.editTopic = findViewById(R.id.editTopic)
        this.saveTask = findViewById(R.id.saveTask)
        this.taskImageView = findViewById(R.id.imageView)


        val taskId = intent.getIntExtra("taskId", -1)
        val isUpdate = intent.getBooleanExtra("isUpdate", false)
        val contactUserId = intent.getIntExtra("contactUserId", -1)
        val color = intent.getStringExtra("color")


        if (color != null) {
            supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(color)))
            window.statusBarColor = ColorUtils.blendARGB(
                Color.parseColor(color),
                Color.BLACK,
                0.4f
            )
            saveTask.setBackgroundColor(Color.parseColor(color))
        }


        if (isUpdate) {
            editTitle.setText(intent.getStringExtra("title").toString())
            setTimer.setText(intent.getIntExtra("timer", 0).toString())
            editTopic.setText(intent.getStringExtra("topic").toString())
            taskImageView.setImageBitmap(intent.getParcelableExtra("img"))
            this.findViewById<TextView>(R.id.saveTask).text = "Päivitä"
            supportActionBar?.title = "Muokkaa tehtävää"
        } else {
            supportActionBar?.title = "Lisää uusi tehtävä"
        }

        saveTask.setOnClickListener {
            val title = editTitle.text.toString()
            val timer = setTimer.text.toString()
            val topic = editTopic.text.toString()

            if (timer.isNotEmpty()) {
                val alarm = AlarmHandler(this)

                alarm.start(title, timer, topic)

                val requestCode = alarm.getRequestCode()
                val daysRemain = alarm.getDaysDifference()
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
                data.putExtra("isUpdate", isUpdate)

                setResult(RESULT_OK, data)

                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Aseta ajastimeen tehtävän ilmoitusten aikaväli",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Check and ask for permissions, then start gallery activity.
        taskImageView.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) -> {
                    // You can use the API that requires the permission.
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    ActivityCompat.startActivityForResult(
                        this as Activity,
                        takePictureIntent,
                        1002,
                        null
                    )
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.getBooleanExtra("isUpdate", false)) {
            val inflater = menuInflater
            inflater.inflate(R.menu.edit_task_menu, menu)
        }
        return true
    }

    // Determine if action bar item was selected. If true do corresponding action.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle presses on the action bar menu.
        return when (item.itemId) {
            R.id.action_delete_task -> {
                val deleteId = intent.getIntExtra("taskId", 0)
                val intent = Intent(this, TaskActivity::class.java)

                intent.putExtra("isDelete", true)
                intent.putExtra("taskId", deleteId)
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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