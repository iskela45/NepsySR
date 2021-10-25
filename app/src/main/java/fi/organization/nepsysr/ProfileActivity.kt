package fi.organization.nepsysr

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import fi.organization.nepsysr.database.*

class ProfileActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var btSave : Button
/*
    Näitä ei ainakaan vielä käytetä missään. Ovat tulleet vahingossa?

    val adapter = ContactListAdapter()

    private val contactViewModel: ContactViewModel by viewModels {
        ContactViewModelFactory((application as AppApplication).repository)
    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.etName = findViewById(R.id.etName)
        this.btSave = findViewById(R.id.btSave)

        btSave.setOnClickListener {
            val name = etName.text.toString()

            val data = Intent()
            data.putExtra("name", name)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }
}