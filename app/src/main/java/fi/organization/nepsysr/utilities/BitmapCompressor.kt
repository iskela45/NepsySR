package fi.organization.nepsysr.utilities

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun compressBitmap(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
