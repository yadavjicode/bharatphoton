package app.bharatphoton.service.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import app.bharatphoton.service.R
import app.bharatphoton.service.ui.activities.HomeActivity
import app.bharatphoton.service.ui.fragments.NotificationFragment
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        val activityIntent = Intent(this,FirebaseBroadcastReceiver::class.java)
        val notificationData = JSONObject()
        notificationData.toString().mLog()
        intent?.extras?.keySet()?.forEach {
            "$it->"+intent.extras?.get(it).toString().mLog()
            activityIntent.putExtra(it,intent.extras?.get(it).toString())
        }
        sendBroadcast(activityIntent)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "Default Title"
        val body = remoteMessage.notification?.body ?: "Default Body"

        createNotification(title, body)
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotification(title: String, body: String) {

        val channelId = getString(R.string.firebase_default_notification_channel_id)
        val channelName = getString(R.string.firebase_default_notification_channel_id)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel != null) {
                notificationManager.deleteNotificationChannel(channelId)
            }
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(this, NotificationFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_icon_small)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationManager.notify(1, notificationBuilder.build())
    }
}

class FirebaseBroadcastReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("gcm.notification.title") ?: "Default Title"
        val body = intent?.getStringExtra("gcm.notification.body") ?: "Default Body"

        createNotification(context,title, body)
    }
    @SuppressLint("ObsoleteSdkInt")
    private fun createNotification(context: Context?, title: String, body: String) {
        val channelId = context?.getString(R.string.firebase_default_notification_channel_id) ?: "default_channel"
        val channelName = context?.getString(R.string.firebase_default_notification_channel_id) ?: "default_channel"

        val notificationManager = context?.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel != null) {
                notificationManager.deleteNotificationChannel(channelId)
            }
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_icon_small)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(null)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationManager.notify(1, notificationBuilder.build())
    }
}