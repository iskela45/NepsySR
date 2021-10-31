package fi.organization.nepsysr.database

import android.graphics.Bitmap
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class AppRepository(private val appDao: AppDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allContacts: Flow<List<Contact>> = appDao.getAllContacts()

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allTasks: Flow<List<Task>> = appDao.getAllTasks()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(contact: Contact) {
        appDao.insert(contact)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        appDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateContactImage(uid: Int, img: Bitmap) {
        appDao.updateContactImage(ContactImageUpdate(uid, img))
    }


    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTask(task: Task) {
        appDao.insertTask(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllTasks() {
        appDao.deleteAllTasks()
    }
}
