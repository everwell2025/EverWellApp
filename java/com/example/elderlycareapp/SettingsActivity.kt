package com.example.elderlycareapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : BaseActivity() {

    private lateinit var logoutbutton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBottomNavigation(R.id.nav_settings)    // For SettingsActivity

        findViewById<ImageButton>(R.id.set_bill_button).setOnClickListener {
            startActivity(Intent(this, BillReminderActivity::class.java))
        }

        findViewById<ImageButton>(R.id.med_reminder_button).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }

        logoutbutton = findViewById(R.id.logoutbutton)


        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)


        logoutbutton.setOnClickListener {
            val editor = prefs.edit()
            editor.clear()
            editor.apply()


            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}