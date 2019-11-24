package com.example.mysharedbooking.helpers

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.mysharedbooking.R


private val notificationId = 19

fun NotificationManager.sendNotification(context: Context, title: String, body: String){

    val notificationBuilder = NotificationCompat
        .Builder( context, context.getString(R.string.book_channel_id))
        .setContentTitle(title)
        .setContentText(body)
        .setSmallIcon(R.mipmap.dark_app_icon)

    notify(notificationId, notificationBuilder.build())


}