package fi.organization.nepsysr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.database.*
import fi.organization.nepsysr.utilities.compressBitmap

class TaskActivity : AppCompatActivity() {


    lateinit var title: String
    lateinit var timer: String
    private lateinit var topic: String
    lateinit var img: String
    private lateinit var taskResult: ActivityResultLauncher<Intent>
    lateinit var color: String


    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as AppApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // Placeholder image
        val drawable =
            AppCompatResources.getDrawable(this, R.drawable.ic_baseline_add_a_photo_124)
        val placeholderBitmap = drawable?.toBitmap()

        val contactUid = intent.getIntExtra("uid", -1)
        color = intent.getStringExtra("color").toString()

        supportActionBar?.title = intent.getStringExtra("name").toString()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(color)))
        window.statusBarColor = ColorUtils.blendARGB(
            Color.parseColor(color),
            Color.BLACK,
            0.4f
        )


        this.taskResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                title = result.data?.getStringExtra("title").toString()
                timer = result.data?.getStringExtra("timer").toString()
                topic = result.data?.getStringExtra("topic").toString()
                img = result.data?.getStringExtra("img").toString()

                val requestCode = result.data?.getIntExtra("requestCode", 0)
                val daysRemain = result.data?.getIntExtra("daysRemain", 0)
                val timerInt = Integer.parseInt(timer)

                val byteArray = result.data?.getByteArrayExtra("img")
                val img = byteArray?.let {
                    BitmapFactory.decodeByteArray(byteArray, 0, it.size)
                }

                val task : Task = if (img != null) {
                    Task(
                        0, contactUid, title, timerInt, topic,
                        img, requestCode!!, daysRemain!!
                    )
                } else {
                    Task(
                        0, contactUid, title, timerInt, topic,
                        placeholderBitmap!!, requestCode!!, daysRemain!!
                    )
                }
                taskViewModel.insertTask(task)

            }

        }

        // Create recyclerViews for all of the names.
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerviewTasks)
        val adapter = TaskListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observes changes to the data and updates the GUI accordingly
        taskViewModel.getFilteredList(contactUid).observe(this) { tasks ->
            // Update the cached copy of the contacts in the adapter.
            tasks.let { adapter.submitList(it) }
        }
    }

    /**
     * Receive image from camera, use request code to identify the contact.
     * This solution is really hacky but since this is a prototype it doesn't really matter.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val extraCheck = data?.getStringExtra("title")
        val taskId = data?.getIntExtra("taskId", -1)
        val cameraBitmap = data?.extras?.get("data") as Bitmap?

        if(extraCheck == null && cameraBitmap != null){
            taskViewModel.updateTaskImage(requestCode, compressBitmap(cameraBitmap))
            return

        } else if(extraCheck == null && data?.data != null){
            val uriImg = data.data
            val galleryBitmap : Bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uriImg)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uriImg!!)
                ImageDecoder.decodeBitmap(source)
            }

            taskViewModel.updateTaskImage(requestCode, compressBitmap(galleryBitmap))
            return
        }

        val isUpdate = data?.getBooleanExtra("isUpdate", false)
        if (isUpdate == true) {
            title = data.getStringExtra("title").toString()
            timer = data.getStringExtra("timer").toString()
            val timerInt = timer.toInt()
            topic = data.getStringExtra("topic").toString()
            val contactUid = data.getIntExtra("contactUserId", -1)
            val daysRemain = data.getIntExtra("daysRemain", 0)

            val byteArray = data.getByteArrayExtra("img")
            val img = byteArray?.let { BitmapFactory.decodeByteArray(byteArray, 0, it.size) }

            if(taskId != null && img != null) {
                val taskToUpdate = Task(
                    taskId, contactUid, title, timerInt, topic,
                    img, requestCode, daysRemain
                )
                taskViewModel.updateTask(taskToUpdate)
            }
        }

        val isDelete = data?.getBooleanExtra("isDelete", false)
        if (taskId != null) {
            if (isDelete == true && taskId > 0) taskViewModel.deleteTaskById(taskId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        // Inflate the menu; this adds items to the action bar if it's present.
        inflater.inflate(R.menu.task_menu, menu)
        return true
    }

    // Determine if action bar item was selected. If true do corresponding action.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // handle presses on the action bar menu.
        return when (item.itemId) {
            R.id.action_add_task -> {
                val intent = Intent(this, AddingTaskActivity::class.java)
                intent.putExtra("color", color)
                taskResult.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

