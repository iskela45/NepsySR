package fi.organization.nepsysr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.organization.nepsysr.database.*

class TaskActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as AppApplication).repository)
    }

    lateinit var title: String
    lateinit var timer: String
    lateinit var topic: String
    lateinit var img: String

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
            val task = Task(0, 0, title, timerInt, topic, img, requestCode!!, daysRemain!!)
            taskViewModel.insertTask(task)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // Create recyclerViews for all of the names.
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerviewTasks)
        val adapter = TaskListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observes changes to the data and updates the GUI accordingly
        taskViewModel.allTasks.observe(this) { tasks ->
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

