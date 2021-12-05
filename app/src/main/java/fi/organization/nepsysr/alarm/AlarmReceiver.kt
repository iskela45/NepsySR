package fi.organization.nepsysr.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import fi.organization.nepsysr.MainActivity
import fi.organization.nepsysr.R

class AlarmReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "ChannelId"
    private val CHANNEL_NAME = "ChannelName"

    @SuppressLint("WrongConstant", "UnspecifiedImmutableFlag")
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getIntExtra("notificationId", 0)
        val title = intent?.getStringExtra("title")
        val topic = intent?.getStringExtra("topic")
        val days = intent?.getIntExtra("days", 0)

        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val notificationsOnOrOff = sp.getBoolean("disable_notifications", false)

        if (days!! == 0 && !notificationsOnOrOff) {
            createNotificationChannel(context)

            // When notification is tapped, call ProfileActivity
            val intent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(context, 0, intent, 0)

            var notificationManager : NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create notification with channel Id
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(topic)
                .setPriority(NotificationCompat.PRIORITY_MAX)
            builder.setContentIntent(contentIntent).setAutoCancel(true)

            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            with(mNotificationManager) {
                notify(notificationId!!, builder.build())
            }
        }
    }

    private fun createNotificationChannel(context : Context?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT.apply {
                }
            )
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}