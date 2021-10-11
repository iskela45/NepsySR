package fi.organization.nepsysr.ContactDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// Annotates class to be a Room Database with a table (entity) of the contact class
@Database(entities = arrayOf(Contact::class), version = 1, exportSchema = false)
public abstract class ContactRoomDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ContactRoomDatabase? = null

        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): ContactRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactRoomDatabase::class.java,
                    "contact_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ContactDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class ContactDatabaseCallback(
                private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.contactDao())
                    }
                }
            }


        }

        suspend fun populateDatabase(contactDao: ContactDao) {
            // Clearing the previous data.
            contactDao.deleteAll()

            // Adding placeholder contacts.
            var contact = Contact(0, "Zezima", "img1", "#000000", 0)
            contactDao.insert(contact)
            contact = Contact(0, "Kekkonen", "img2", "#000000", 0)
            contactDao.insert(contact)
        }
    }
}

//@Database(entities = arrayOf(Contact::class), version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun contactDao(): ContactDao
//}
