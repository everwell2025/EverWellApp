package com.example.elderlycareapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NotificationsActivity : BaseActivity() {


    private lateinit var rvNotifications: RecyclerView
    private lateinit var upcomingReminders: List<Reminder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationss)

        setupBottomNavigation(R.id.nav_notifications) // For NotificationsActivity

        rvNotifications = findViewById(R.id.rv_notifications)

        upcomingReminders = loadReminders().filter { it.isUpcoming() }


        if (upcomingReminders.isEmpty()) {
            Toast.makeText(this, "No upcoming reminders", Toast.LENGTH_SHORT).show()
        }


        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = ReminderAdapter(upcomingReminders.toMutableList()) { reminder, _ ->
            Toast.makeText(this, "Reminder: ${reminder.medicine}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadReminders(): List<Reminder> {
        val sharedPreferences = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val remindersJson = sharedPreferences.getString("reminders", "[]")
        return Reminder.fromJsonList(remindersJson!!)
    }
}
