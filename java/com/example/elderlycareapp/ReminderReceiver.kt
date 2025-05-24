package com.example.elderlycareapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medication = intent.getStringExtra("medicineName") ?: return


        val builder = NotificationCompat.Builder(context, "medication_reminder")
            .setSmallIcon(R.drawable.pill)
            .setContentTitle("Medication Reminder")
            .setContentText("Time to take: $medication")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)


        val notificationManager = NotificationManagerCompat.from(context)


        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify((medication + System.currentTimeMillis()).hashCode(), builder.build())
        }
    }
}
