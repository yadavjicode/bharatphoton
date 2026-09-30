package com.app.bharatphoton.firebaseMessagingService

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.bharatphoton.activity.MainActivity
import com.app.bharatphoton.R
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        SharedData.notificationCount += 1
        val title = message.notification?.title ?: "Notification"
        val body = message.notification?.body ?: "You have new message"
        val channelId = "Notification"
        val channelName = "NotificationChannel"
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        NotificationManage.increaseNotificationCount()


        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId).setContentTitle(title).setContentText(body).setPriority(NotificationCompat.PRIORITY_HIGH).setStyle(NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setSmallIcon(R.drawable.logo_).setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentIntent(pendingIntent).setChannelId(channelId).setAutoCancel(false)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for general notifications"
                enableLights(true)
                lightColor = ContextCompat.getColor(this@FirebaseMessagingService, R.color.white)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

}