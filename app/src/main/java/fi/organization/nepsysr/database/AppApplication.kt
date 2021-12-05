package fi.organization.nepsysr.database

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// ApplicationApplication extends Application. Hehe
class AppApplication : Application() {

    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts (So mostly just for first startup)
    val database by lazy { AppRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AppRepository(database.appDao()) }
}
