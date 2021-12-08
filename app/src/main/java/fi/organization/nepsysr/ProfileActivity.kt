package fi.organization.nepsysr

import ColorPickerFragment
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import fi.organization.nepsysr.utilities.ProfileInterface
import fi.organization.nepsysr.utilities.compressBitmap
import fi.organization.nepsysr.utilities.convertBitmap


class ProfileActivity : AppCompatActivity(), ProfileInterface {

    private lateinit var etName: EditText
    private lateinit var btSave : Button
    private lateinit var tvHeading : TextView
    private lateinit var btPickColor : Button
    private lateinit var selectedColor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.etName = findViewById(R.id.etName)
        this.btSave = findViewById(R.id.btSave)
        this.tvHeading = findViewById(R.id.tvHeading)
        this.btPickColor = findViewById(R.id.btPickColor)
        val contactImageView: ImageView = findViewById(R.id.imageView)
        Log.d("TAG", "wat1 ${intent.getIntExtra("uid", 0)}")
        
        val isUpdate : Boolean = intent.getBooleanExtra("isUpdate", false)
        
        if (isUpdate) {
            val editName = intent.getStringExtra("name")
            val serializedImage = intent.getSerializableExtra("img")
            // TODO: set color for colorPicker when that feature is done
            val editColor = intent.getStringExtra("color")
            contactImageView.setImageBitmap(BitmapFactory.decodeByteArray(
                serializedImage as ByteArray?,
                0,
                serializedImage!!.size
            ))

            etName.setText(editName)
            tvHeading.text = "Muokkaa"
        }

        btPickColor.setOnClickListener {
            var dialog = ColorPickerFragment()

            dialog.show(supportFragmentManager, "colorPickerDialog")
        }

        btSave.setOnClickListener {
            val name = etName.text.toString()
            val drawable = contactImageView.drawable
            val bitmap = drawable.toBitmap()
            val data = Intent()
            var uid = 0
            var color = selectedColor
            if (isUpdate) {
                uid = intent.getIntExtra("uid", 0)
                Log.d("TAG", "wat2 ${intent.getIntExtra("uid", 0)}")
                color = intent.getStringExtra("color").toString()
            }

            data.putExtra("uid", uid)
            data.putExtra("name", name)
            data.putExtra("img", convertBitmap(bitmap))
            data.putExtra("color", color)
            data.putExtra("isUpdate", isUpdate)

            setResult(Activity.RESULT_OK, data)
            finish()
        }

        // Check and ask for permissions, then start gallery activity.
        contactImageView.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    // You can use the API that requires the permission.
                    val gallery = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    )

                    ActivityCompat.startActivityForResult(
                        this as Activity,
                        gallery,
                        1001,
                        null
                    )
                }
                else -> {
                    // You can directly ask for the permission.
                    ActivityCompat.requestPermissions(
                        this as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1001
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (intent.getBooleanExtra("isUpdate", false)) {
            val inflater = menuInflater
            inflater.inflate(R.menu.profile_menu, menu)
        }
        return true
    }

    // Determine if action bar item was selected. If true do corresponding action.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle presses on the action bar menu.
        return when (item.itemId) {
            R.id.action_delete_contact -> {
                val deleteId = intent.getIntExtra("uid", 0)
                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("isDelete", true)
                intent.putExtra("uid", deleteId)
                setResult(Activity.RESULT_OK, intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1001 && data?.data != null){
            val uriImg = data.data

            val bitmap : Bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, uriImg)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uriImg!!)
                ImageDecoder.decodeBitmap(source)
            }

            val img : ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(compressBitmap(bitmap))
        }
    }

    override fun passData(profileColor: String) {
        selectedColor = profileColor
    }
}
