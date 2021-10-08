package fi.organization.nepsysr.ContactDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    var name: String,
    var img: String,
    var color: String,
    var points: Int) {

}