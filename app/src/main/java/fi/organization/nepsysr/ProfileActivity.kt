package fi.organization.nepsysr

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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import fi.organization.nepsysr.utilities.compressBitmap
import fi.organization.nepsysr.utilities.convertBitmap


class ProfileActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var btSave : Button
    lateinit var tvHeading : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.etName = findViewById(R.id.etName)
        this.btSave = findViewById(R.id.btSave)
        this.tvHeading = findViewById(R.id.tvHeading)
        var contactImageView: ImageView = findViewById(R.id.imageView)
        
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

        btSave.setOnClickListener {
            val name = etName.text.toString()
            val drawable = contactImageView.drawable
            val bitmap = drawable.toBitmap()
            val data = Intent()
            var uid : Int = 0
            var color = "#49ba54"
            if (isUpdate) {
                uid = intent.getIntExtra("uid", 0)
                color = intent.getStringExtra("color").toString()
            }

            data.putExtra("uid", uid)
            data.putExtra("name", name)
            data.putExtra("img", convertBitmap(bitmap))
            data.putExtra("color", color)
            data.putExtra("isUpdate", isUpdate)

            setResult(Activity.RESULT_OK, data)
            Log.d("TAG", "saveTest: ${name}")
            finish()
        }

        // Check and ask for permissions, then start gallery activity.
        contactImageView.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1001 && data?.data != null){
            var uriImg = data?.data
            lateinit var bitmap : Bitmap

            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriImg)
            } else {
                val source = ImageDecoder.createSource(contentResolver, uriImg!!)
                bitmap = ImageDecoder.decodeBitmap(source)
            }

            val img : ImageView = findViewById(R.id.imageView)
            img.setImageBitmap(compressBitmap(bitmap))
        }
    }
}
