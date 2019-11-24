package com.example.mysharedbooking

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.mysharedbooking.helpers.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {

        super.onNewToken(p0)
        println(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        showNotification( p0.notification?.title!! ,p0.notification?.body!!)
    }

    private fun showNotification( title: String, message: String){
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        notificationManager.sendNotification(applicationContext, title, message )
    }
}