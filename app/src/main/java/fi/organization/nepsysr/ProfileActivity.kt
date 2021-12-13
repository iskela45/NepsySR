package fi.organization.nepsysr

import ColorPickerFragment
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import fi.organization.nepsysr.utilities.ProfileInterface
import fi.organization.nepsysr.utilities.compressBitmap
import fi.organization.nepsysr.utilities.convertBitmap


class ProfileActivity : AppCompatActivity(), ProfileInterface {

    private lateinit var etName: EditText
    private lateinit var btSave : Button
    private lateinit var btPickColor : Button
    private lateinit var selectedColor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.selectedColor = "#555555"
        this.etName = findViewById(R.id.etName)
        this.btSave = findViewById(R.id.btSave)
        this.btPickColor = findViewById(R.id.btPickColor)
        val contactImageView: ImageView = findViewById(R.id.imageView)
        
        val isUpdate : Boolean = intent.getBooleanExtra("isUpdate", false)
        
        if (isUpdate) {
            val editName = intent.getStringExtra("name")
            val serializedImage = intent.getSerializableExtra("img")
            val editColor = intent.getStringExtra("color")

            contactImageView.setImageBitmap(BitmapFactory.decodeByteArray(
                serializedImage as ByteArray?,
                0,
                serializedImage!!.size
            ))

            supportActionBar?.title = "Muokkaa kontaktia"
            setColors(editColor!!)
            etName.setText(editName)
            selectedColor = editColor
        } else {
            supportActionBar?.title = "Lisää kontakti"
            setColors(selectedColor)
        }

        btPickColor.setOnClickListener {
            var dialog = ColorPickerFragment()
            dialog.show(supportFragmentManager, "colorPickerDialog")
        }

        btSave.setOnClickListener {
            val name = etName.text.toString()
            if (name != "") {
                val drawable = contactImageView.drawable
                val bitmap = drawable.toBitmap()
                val data = Intent()
                var uid = 0
                if (isUpdate) {
                    uid = intent.getIntExtra("uid", 0)
                }

                data.putExtra("uid", uid)
                data.putExtra("name", name)
                data.putExtra("img", convertBitmap(bitmap))
                data.putExtra("color", selectedColor)
                data.putExtra("isUpdate", isUpdate)

                setResult(Activity.RESULT_OK, data)
                finish()

            } else {
                Toast.makeText(
                    applicationContext,
                    "Nimikenttä on tyhjä",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val addImageDialog = AlertDialog.Builder(this)
            .setTitle("Lisää kuva")
            .setMessage("Lisätäänkö kuva kamerasta vai galleriasta?")
            .setPositiveButton("Kamera") { _, _ ->
                openCamera()
            }
            .setNegativeButton("Galleria") { _, _ ->
                openGallery()
            }

        // Check and ask for permissions, then start gallery activity.
        contactImageView.setOnClickListener {
            addImageDialog.show()
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
        val bitmap = data?.extras?.get("data") as Bitmap?

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
        } else if (requestCode == 1002 && bitmap != null) {
            val img : ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(compressBitmap(bitmap))
        }
    }

    private fun setColors(newColor: String) {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(newColor)))
        window.statusBarColor = ColorUtils.blendARGB(
            Color.parseColor(newColor),
            Color.BLACK,
            0.4f
        )
        btPickColor.setBackgroundColor(Color.parseColor(newColor))
        btSave.setBackgroundColor(Color.parseColor(newColor))

    }

    override fun passData(profileColor: String) {
        selectedColor = profileColor
        setColors(profileColor)
    }

    private fun openCamera() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // You can use the API that requires the permission.
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                ActivityCompat.startActivityForResult(
                    this as Activity,
                    takePictureIntent,
                    1002,
                    null
                )
            }
            else -> {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(
                    this as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    1002
                )
            }
        }
    }

    private fun openGallery() {
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
