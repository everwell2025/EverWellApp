package com.example.elderlycareapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat


class BillReminderBroadcast : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val channelId = "reminderChannel"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }


        val billType = intent.getStringExtra("billType") ?: "Bill Reminder"
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val repeat = intent.getStringExtra("repeat") ?: "Never"


        val notificationIntent = Intent(context, BillReminderListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("billType", billType)
            putExtra("date", intent.getStringExtra("date"))
            putExtra("time", intent.getStringExtra("time"))
            putExtra("repeat", intent.getStringExtra("repeat"))
        }


        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bell)  // Replace with your icon
            .setContentTitle("Reminder: $billType")
            .setContentText("It's time to pay your $billType bill.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
