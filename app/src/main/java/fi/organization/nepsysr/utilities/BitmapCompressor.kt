package fi.organization.nepsysr.utilities

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun compressBitmap(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}
