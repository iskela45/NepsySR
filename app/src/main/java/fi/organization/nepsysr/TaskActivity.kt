package fi.organization.nepsysr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.database.*
import fi.organization.nepsysr.utilities.TaskColorInterface

class TaskActivity : AppCompatActivity() {


    lateinit var title: String
    lateinit var timer: String
    private lateinit var topic: String
    lateinit var img: String
    private lateinit var taskResult: ActivityResultLauncher<Intent>


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
        val color = intent.getStringExtra("color").toString()
        //window.statusBarColor(Color.parseColor(color))
        //this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor == Color.parseColor("#000000")
        this.window.navigationBarColor == Color.parseColor("#000000")
        window.statusBarColor = Color.BLUE

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
        //findViewById<RecyclerView>(R.id.task_View).setBackgroundColor(Color.parseColor(color))
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerviewTasks)
        //recyclerView.findViewById<LinearLayout>(R.id.task_View).setBackgroundColor(Color.parseColor("#000000"))
        val adapter = TaskListAdapter()
        //val taskLayout: LinearLayout =
        //taskLayout.setBackgroundColor(Color.parseColor(color))
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
        val imageBitmap = data?.extras?.get("data") as Bitmap?

        if(extraCheck == null && imageBitmap != null){
            taskViewModel.updateTaskImage(requestCode, imageBitmap)
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
                taskResult.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

