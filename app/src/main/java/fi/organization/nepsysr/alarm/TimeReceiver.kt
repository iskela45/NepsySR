package fi.organization.nepsysr.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fi.organization.nepsysr.database.AppRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.concurrent.thread

class TimeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val applicationScope = CoroutineScope(SupervisorJob())
        val database = AppRoomDatabase.getDatabase(context!!, applicationScope)
        val alarm = AlarmHandler(context)

        thread {
            var tasks = database.appDao().getAllTasksList()

            // update the daysRemain-column of the task by subtracting one day
            for (i in tasks) {
                database.appDao().updateDaysRemain(i.taskId)
            }
            // update notification
            alarm.updateAlarm()
        }
    }

}
