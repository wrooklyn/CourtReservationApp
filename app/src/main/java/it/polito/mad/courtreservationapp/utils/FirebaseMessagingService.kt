package it.polito.mad.courtreservationapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage
import it.polito.mad.courtreservationapp.R


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("Message received :")
        println(remoteMessage.data)
        println(remoteMessage.messageId)
        println(remoteMessage.notification?.title)
        println(remoteMessage.notification?.body)

        super.onMessageReceived(remoteMessage)

        // Create and display the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a unique notification channel ID for Android Oreo and above
        val channelId = "my_channel_id"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Court reservation channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notification Channel for the Court Reservation App"
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(R.drawable.gesu)
            .setAutoCancel(true)

        // Display the notification
        val notificationId = 1 // Unique ID for the notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // Handle the token refresh event
        // You can update the token in Firebase Firestore or your backend
        // ...
    }
}