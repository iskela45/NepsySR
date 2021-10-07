package fi.organization.nepsysr.ContactDatabase

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ContactsApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts (So mostly just for first startup)
    val database by lazy { ContactRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ContactRepository(database.contactDao()) }
}
