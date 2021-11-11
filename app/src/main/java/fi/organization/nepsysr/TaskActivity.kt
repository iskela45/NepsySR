package fi.organization.nepsysr

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.organization.nepsysr.database.*

class TaskActivity : AppCompatActivity() {


    lateinit var title: String
    lateinit var timer: String
    lateinit var topic: String
    lateinit var img: String
    lateinit var taskResult: ActivityResultLauncher<Intent>

    // Moved here, because it is required in 2 places


    val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as AppApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        Log.d("TAG", "taski auki")

        // Placeholder image
        val drawable = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_image_search_24)
        val placeholderBitmap = drawable?.toBitmap()

        var contactUid = intent.getIntExtra("uid", -1)

        this.taskResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                title = result.data?.getStringExtra("title").toString()
                timer = result.data?.getStringExtra("timer").toString()
                topic = result.data?.getStringExtra("topic").toString()
                img = result.data?.getStringExtra("img").toString()
                val requestCode = result.data?.getIntExtra("requestCode", 0)
                var daysRemain = result.data?.getIntExtra("daysRemain", 0)

                val timerInt = Integer.parseInt(timer)
                val task = Task(0, contactUid, title, timerInt, topic, placeholderBitmap!!, requestCode!!, daysRemain!!)
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

        /*
        val addTask = findViewById<FloatingActionButton>(R.id.addNewTask)
        addTask.setOnClickListener {
            val intent = Intent(this, AddingTaskActivity::class.java)
            taskResult.launch(intent)
        }
         */
    }

    /**
     * Receive image from camera, use request code to identify the contact.
     * This solution is really hacky but since this is a prototype it doesn't really matter.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var extraCheck = data?.getStringExtra("title")
        val imageBitmap = data?.extras?.get("data") as Bitmap?

        if(extraCheck == null && imageBitmap != null){
            taskViewModel.updateTaskImage(requestCode, imageBitmap!!)
        }

        // resultcode 2000 is for update
        if (resultCode == 2000) {
            title = data?.getStringExtra("title").toString()
            timer = data?.getStringExtra("timer").toString()
            var timerInt = timer.toInt()
            topic = data?.getStringExtra("topic").toString()
            img = data?.getStringExtra("img").toString()
            var taskId = data?.getIntExtra("taskId", -1)
            var contactUid = data?.getIntExtra("contactUserId", -1)
            var daysRemain = data?.getIntExtra("daysRemain", 0)
            // Placeholder image
            val drawable = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_image_search_24)
            val placeholderBitmap = drawable?.toBitmap()
            Log.d("TAG", "$taskId")
            if(taskId != null && contactUid != null && timer != null){
                var taskToUpdate = Task(taskId, contactUid, title, timerInt, topic, placeholderBitmap!!, requestCode!!, daysRemain!!)
                taskViewModel.updateTask(taskToUpdate)
            }
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

