package fi.organization.nepsysr.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    var name: String,
    var img: String,
    var color: String,
    var points: Int)

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int,
    var contactId: Int,
    var title: String,
    var timer: Int,
    val topic: String,
    var img: String,
)

/*
data class ContactWithTasks(
        @Embedded val contact: Contact,
        @Relation(
                parentColumn = "uid",
                entityColumn = "contactId"
        )
        val tasks: List<Task>
)*/
