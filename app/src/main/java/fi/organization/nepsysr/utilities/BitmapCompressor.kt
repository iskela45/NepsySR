package fi.organization.nepsysr.utilities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import java.io.ByteArrayOutputStream

fun convertBitmap(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    return stream.toByteArray()
}

/**
 * This is since some images are too big for the data parcel causing a crash.
 * If we were to compress in the convertBitmap() function the image would get
 * Compressed with each edit so we only do it onActivityResult.
 */
fun compressBitmap(bitmap: Bitmap): Bitmap {
    val stream = ByteArrayOutputStream()

    if (Build.VERSION.SDK_INT < 30) {
        bitmap.compress(Bitmap.CompressFormat.WEBP, 40, stream)
    } else {
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 40, stream)
    }

    var byteArray = stream.toByteArray()
    return byteArray!!.let { BitmapFactory.decodeByteArray(byteArray, 0, it.size) }
}