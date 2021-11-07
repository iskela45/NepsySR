package fi.organization.nepsysr.database

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fi.organization.nepsysr.R
import fi.organization.nepsysr.utilities.BitmapConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// Annotates class to be a Room Database with a table (entity) of the contact class
@TypeConverters(BitmapConverter::class)
@Database(entities = arrayOf(Contact::class, Task::class), version = 1, exportSchema = false)
public abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): AppRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class AppDatabaseCallback(
                private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.appDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(appDao: AppDao) {
            // Clearing the previous data.
            appDao.deleteAll()

            // Adding placeholder contacts.
            //var contact = Contact(0, "Zezima", "img1", "#e34f32", 0)
            //appDao.insert(contact)
            //contact = Contact(0, "Kekkonen", "img2", "#e34f32", 0)
            //appDao.insert(contact)


            // Needed to disable since Task now requires bitmap for img
            //val task = Task(0, 0, "Soitto", 7, "Lomareissu", "img3", 1, 1)
            //Log.d("TAG", "populate")
            //appDao.insertTask(task)
        }
    }
}
