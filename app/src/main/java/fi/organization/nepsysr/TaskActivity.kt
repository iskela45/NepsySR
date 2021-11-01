package fi.organization.nepsysr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        var contactUid = intent.getIntExtra("uid", -1)

        val taskViewModel: TaskViewModel by viewModels {
            TaskViewModelFactory((application as AppApplication).repository)
        }

        val taskResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                title = result.data?.getStringExtra("title").toString()
                timer = result.data?.getStringExtra("timer").toString()
                topic = result.data?.getStringExtra("topic").toString()
                img = result.data?.getStringExtra("img").toString()
                val requestCode = result.data?.getIntExtra("requestCode", 0)
                var daysRemain = result.data?.getIntExtra("daysRemain", 0)

                val timerInt = Integer.parseInt(timer)
                val task = Task(0, contactUid, title, timerInt, topic, img, requestCode!!, daysRemain!!)
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

        val addTask = findViewById<FloatingActionButton>(R.id.addNewTask)
        addTask.setOnClickListener {
            val intent = Intent(this, AddingTaskActivity::class.java)
            taskResult.launch(intent)
        }
    }
}

