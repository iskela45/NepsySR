package fi.organization.nepsysr.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    var name: String,
    var img: Bitmap,
    var color: String,
    var points: Int)

class ContactImageUpdate (
    var uid: Int,
    var img: Bitmap
)

class ContactEditUpdate (
    var uid: Int,
    var name: String,
    var img: Bitmap,
    var color: String
)

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int,
    var contactId: Int,
    var title: String,
    var timer: Int,
    val topic: String,
    var img: Bitmap,
    var requestCode: Int,
    var daysRemain: Int
)

class TaskImageUpdate (
    var taskId: Int,
    var img: Bitmap
)
