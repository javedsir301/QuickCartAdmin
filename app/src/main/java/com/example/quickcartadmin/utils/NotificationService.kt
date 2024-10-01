package com.example.quickcartadmin.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.quickcartadmin.R
import com.example.quickcartadmin.activity.AdminMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
//class NotificationService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        val channnelId = "Admin Diya Batti"
//        val channel = NotificationChannel(
//            channnelId,
//            "Diya Batti",
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "Diya Batti message"
//            enableLights(true)
//        }
//
//        val  pendingIntent = PendingIntent.getActivity(this,0, Intent(this,AdminMainActivity::class.java),PendingIntent.FLAG_IMMUTABLE)
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        manager.createNotificationChannel(channel)
//
//        val notification = NotificationCompat.Builder(this, channnelId)
//            .setContentTitle(message.data["title"])
//            .setContentText(message.data["body"])
//            .setSmallIcon(R.drawable.app_logo)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        manager.notify(Random.nextInt(), notification)
//    }
//}

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming notifications here
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        showNotification(title, message)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String?, message: String?) {
        val notificationBuilder = NotificationCompat.Builder(this, "ORDER_CHANNEL_ID")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.app_logo) // Your notification icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(0, notificationBuilder.build())
    }
}
