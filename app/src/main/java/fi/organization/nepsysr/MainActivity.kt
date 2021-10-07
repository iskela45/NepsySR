package fi.organization.nepsysr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.organization.nepsysr.ContactDatabase.*

class MainActivity : AppCompatActivity() {

    private val newContactActivityRequestCode = 1
    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((application as ContactsApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        print("here")
        Log.d("create", "onCreateaaaaaaaaaaaaaaaaaaaa")
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ContactListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Just an example of how to add to the database.
        val testButton = findViewById<FloatingActionButton>(R.id.fab)
        testButton.setOnClickListener {
            // use 0 for auto incrementing uid.
            var contact = Contact(0, "ddd", "no image", "#000000", 0)
            contactViewModel.insert(contact)
            Log.d("clickListener", "success")
        }


        contactViewModel.allContacts.observe(this) { contacts ->
            // Update the cached copy of the contacts in the adapter.
            contacts.let { adapter.submitList(it) }
        }

        var contact = Contact(0, "aaa", "no image", "#000000", 0)
        contactViewModel.insert(contact)
        contact = Contact(0, "bbb", "no image", "#000000", 0)
        contactViewModel.insert(contact)



        //var contacts: List<Contact>? = null
        //var test: Contact = Contact(0, "test", "img1", "#111", 0)

        Log.d("createeeeeeeeee", contactViewModel.toString())
        //print(contacts)
    }

}
