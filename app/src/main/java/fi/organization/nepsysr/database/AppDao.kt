package fi.organization.nepsysr.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM contact")
    suspend fun getAll(): List<Contact>

    @Query("SELECT * FROM contact")
    fun getAllContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE uid IN (:contactIds)")
    fun loadAllByIds(contactIds: IntArray): List<Contact>

    @Insert
    fun insertAll(vararg contacts: Contact)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(contact: Contact)

    @Insert
    fun insertContact(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("DELETE FROM contact")
    suspend fun deleteAll()

    @Update
    suspend fun updateContact(contact: Contact)

    @Query("SELECT * FROM contact WHERE uid == :uid")
    suspend fun getContact(uid: Int): Contact

    @Update(entity = Contact::class)
    suspend fun updateContactImage(obj: ContactImageUpdate)

    @Update(entity = Contact::class)
    suspend fun updateContact(obj: ContactEditUpdate)

    // Tasks

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Insert
    fun insertTask2(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task)

    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM task")
    fun getAllTasksList(): List<Task>

    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE task SET daysRemain = daysRemain - 1 WHERE taskId = :id")
    fun updateDaysRemain(id : Int)

    @Query("UPDATE task SET daysRemain = :days WHERE taskId = :id")
    fun updateTimer(id : Int, days : Int)

    @Update(entity = Task::class)
    suspend fun updateTaskImage(obj: TaskImageUpdate)

}
