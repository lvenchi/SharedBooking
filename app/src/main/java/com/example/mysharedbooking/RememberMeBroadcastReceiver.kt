package com.example.mysharedbooking

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.mysharedbooking.helpers.sendNotification
import com.example.mysharedbooking.models.Booking
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class RememberMeBroadcastReceiver : BroadcastReceiver(){

    val REMEMBER_NOTIFICATION: String = "com.example.MySharedBooking.REMEMBER_ME"
    var formatter = SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm:ss", Locale.ITALIAN)

    override fun onReceive(p0: Context?, p1: Intent?) {

        if(p0 != null) {
            if(p1?.action == "com.example.MySharedBooking.REMEMBER_ME"){
                val notificationManager = ContextCompat.getSystemService(
                    p0,
                    NotificationManager::class.java
                ) as NotificationManager
                val date = Calendar.getInstance()
                date.timeInMillis = p1.getLongExtra("appointment_date", 0)

                notificationManager.sendNotification(p0, "REMEMBER",
                    "You have an appointment for ${p1.getStringExtra("booking_type")} " +
                            "with ${p1.getStringExtra("owner_email")} " +
                            "the ${formatter.format(date.timeInMillis)}")
            } else {

                if (p1?.action == Intent.ACTION_BOOT_COMPLETED || p1?.action == "android.intent.action.QUICKBOOT_POWERON") {

                    val userMail: String? = FirebaseAuth.getInstance().currentUser?.email

                    if(userMail != null ) {
                        val pendingResult: PendingResult = goAsync()
                        val asyncTask = Task(pendingResult, p1, userMail, p0 )
                        asyncTask.execute()
                    }
                }
            }
        }
    }

    private class Task(
        private val pendingResult: PendingResult,
        private val intent: Intent,
        private val userMail:String,
        private val context: Context
    ) : AsyncTask<String, Int, List<Booking>>() {

        override fun doInBackground(vararg params: String?): List<Booking> {

            return MainActivity.getInMemoryDatabase(context).userBookingDao().getDeadBookingsOfUserClient(
                userMail
            )
        }

        override fun onPostExecute(userBookingList: List<Booking>) {
            super.onPostExecute(userBookingList)
            val alarmMgr: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val now = Calendar.getInstance().timeInMillis
            for( booking: Booking in userBookingList){
                if( now < booking.date ){
                    val alarmIntent: PendingIntent = Intent(context, RememberMeBroadcastReceiver::class.java).let { intent ->
                        intent.action = "com.example.MySharedBooking.REMEMBER_ME"
                        intent.putExtra("booking_type", booking.type)
                            .putExtra("owner_email", booking.ownerEmail)
                            .putExtra("appointment_date", booking.date)
                        PendingIntent.getBroadcast(context, 0, intent, 0)
                    }

                    alarmMgr?.set(
                        AlarmManager.RTC_WAKEUP,
                        booking.date,
                        alarmIntent
                    )
                }
            }
            // BroadcastReceiver can be recycled.
            pendingResult.finish()
        }
    }
}