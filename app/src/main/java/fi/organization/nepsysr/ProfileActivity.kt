package fi.organization.nepsysr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import android.graphics.Bitmap
import android.graphics.ImageDecoder

import android.os.Build
import fi.organization.nepsysr.utilities.compressBitmap
import fi.organization.nepsysr.utilities.convertBitmap


class ProfileActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var btSave : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        this.etName = findViewById(R.id.etName)
        this.btSave = findViewById(R.id.btSave)
        var contactImageView: ImageView = findViewById(R.id.imageView)

        btSave.setOnClickListener {
            val name = etName.text.toString()
            val drawable = contactImageView.drawable
            val bitmap = drawable.toBitmap()
            val data = Intent()

            data.putExtra("name", name)
            data.putExtra("img", convertBitmap(bitmap))
            setResult(Activity.RESULT_OK, data)
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
