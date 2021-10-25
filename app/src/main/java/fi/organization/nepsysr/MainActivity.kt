package fi.organization.nepsysr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.organization.nepsysr.database.*


class MainActivity : AppCompatActivity() {


    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((application as AppApplication).repository)
    }

    lateinit var name: String
    val profileResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    { result: ActivityResult? ->
        if (result?.resultCode == Activity.RESULT_OK) {
            // Placeholder image
            val drawable = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_image_search_24)
            val placeholderBitmap = drawable?.toBitmap()

            name = result.data?.getStringExtra("name").toString()
            var contact = Contact(0, name, placeholderBitmap!!, "#49ba54", 0)
            contactViewModel.insert(contact)
            Log.d("TAG", "name: ${name}")
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Placeholder image
        val drawable = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_image_search_24)
        val placeholderBitmap = drawable?.toBitmap()

        // Create recyclerViews for all of the names.
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ContactListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)



        // Just an example of how to add to the database.
        val testButton = findViewById<FloatingActionButton>(R.id.fab)
        testButton.setOnClickListener {
            // use 0 for auto incrementing uid.
            //var contact = Contact(0, "ddd", "no image", "#49ba54", 0)
            //contactViewModel.insert(contact)
            Log.d("clickListener", "success")

            val intent = Intent(this, ProfileActivity::class.java)
            profileResult.launch(intent)
        }

        // This button works as a placeholder to go into tasks activity until the
        // functionality is set for going from recyclerview items
        val test = findViewById<Button>(R.id.test)
        test.setOnClickListener {
            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        // Observes changes to the data and updates the GUI accordingly
        contactViewModel.allContacts.observe(this) { contacts ->
            // Update the cached copy of the contacts in the adapter.
            contacts.let { adapter.submitList(it) }
        }
        contactViewModel.deleteAll()

        // More examples of adding to the database, can be freely removed.
        var contact = Contact(0, "aaa", placeholderBitmap!!, "#ffc870", 0)
        contactViewModel.insert(contact)
        contact = Contact(0, "bbb", placeholderBitmap!!, "#6cb9f0", 0)
        contactViewModel.insert(contact)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        // Inflate the menu; this adds items to the action bar if it's present.
        inflater.inflate(R.menu.my_menu, menu)
        return true
    }

    // Determine if action bar item was selected. If true do corresponding action.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // handle presses on the action bar menu.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
        else -> super.onOptionsItemSelected(item)
        }
    }
}
