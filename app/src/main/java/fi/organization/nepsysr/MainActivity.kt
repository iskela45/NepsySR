package fi.organization.nepsysr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
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
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.alarm.AlarmHandler
import fi.organization.nepsysr.database.*
import fi.organization.nepsysr.utilities.compressBitmap


class MainActivity : AppCompatActivity() {


    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((application as AppApplication).repository)
    }

    lateinit var name: String

    private val profileResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                // Placeholder image
                val drawable = AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_baseline_image_search_124
                )

                name = result.data?.getStringExtra("name").toString()
                val color = result.data?.getStringExtra("color").toString()

                val placeholderBitmap = drawable?.toBitmap()
                val byteArray = result.data?.getByteArrayExtra("img")
                val img = byteArray?.let {
                    BitmapFactory.decodeByteArray(byteArray, 0, it.size)
                }

                val contact = if (img != null) {
                    Contact(0, name, img, color, 0)
                } else {
                    Contact(0, name, placeholderBitmap!!, color, 0)
                }

                contactViewModel.insert(contact)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "SOMU"

        val alarm = AlarmHandler(this)
        alarm.timeObserve()

        // Create recyclerViews for all of the names.
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ContactListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observes changes to the data and updates the GUI accordingly
        contactViewModel.allContacts.observe(this) { contacts ->
            // Update the cached copy of the contacts in the adapter.
            contacts.let { adapter.submitList(it) }
        }
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
            // Just an example of how to add to the database.
            R.id.action_add_contact -> {
                val intent = Intent(this, ProfileActivity::class.java)
                profileResult.launch(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Receive image from gallery, use request code to identify the contact.
     * This solution is really hacky but since this is a prototype it doesn't really matter.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // This is the hacky part, checking that the activity result isn't from contact creation
        // By checking if the name extra exists.
        val extraCheck = data?.getStringExtra("name")
        if(extraCheck == null && data?.data != null){
            val uriImg = data.data

            val bitmap : Bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uriImg)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uriImg!!)
                ImageDecoder.decodeBitmap(source)
            }

            contactViewModel.updateContactImage(requestCode, compressBitmap(bitmap))
        }

        val uid = data?.getIntExtra("uid", 0)
        val isUpdate = data?.getBooleanExtra("isUpdate", false)
        if (isUpdate == true) {
            val newName = data.getStringExtra("name").toString()
            val newColor = data.getStringExtra("color").toString()
            val byteArray = data.getByteArrayExtra("img")
            val newImg = byteArray?.let {
                BitmapFactory.decodeByteArray(byteArray, 0, it.size)
            }
            contactViewModel.updateContact(uid!!, newName, newImg!!, newColor)
        }

        val isDelete = data?.getBooleanExtra("isDelete", false)
        if (uid != null) {
            if (isDelete == true && uid > 0) contactViewModel.deleteContactById(uid)
        }
    }
}

