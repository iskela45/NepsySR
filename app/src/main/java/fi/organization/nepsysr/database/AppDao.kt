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

    // Tasks

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Insert
    fun insertTask2(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task)

    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()

    //@Transaction
    //@Query("SELECT * FROM contact")
    //un getContactWithTasks(): List<ContactWithTasks>
}
