package com.example.elderlycareapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

class MainActivity : BaseActivity() {

    private lateinit var infoButton: ImageButton
    private lateinit var closePopupButton: ImageButton
    private lateinit var popupLayout: View

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted: notifications will work
            } else {
                // Permission denied: you can inform the user or disable notification features
            }
        }

    private lateinit var namedisplay: TextView

    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigation(R.id.nav_home)        // For MainActivity

        namedisplay = findViewById(R.id.namedisplay)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = prefs.getString("username", "User")

        namedisplay.text = "Welcome, $username!"

        infoButton = findViewById(R.id.infoButton)
        closePopupButton = findViewById(R.id.closePopupButton)
        popupLayout = findViewById(R.id.infoPopupLayout)

        infoButton.setOnClickListener {
            popupLayout.visibility = View.VISIBLE
        }

        closePopupButton.setOnClickListener {
            popupLayout.visibility = View.GONE
        }

        // Create notification channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminderChannel",
                "Reminder Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for reminder notifications"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        checkNotificationPermission()

        // Feature Buttons
        findViewById<ImageButton>(R.id.btnGames).setOnClickListener {
            startActivity(Intent(this, GamesOptionsActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnHealthyDiet).setOnClickListener {
            startActivity(Intent(this, FoodCategoryActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnMedicineReminder).setOnClickListener {
            startActivity(Intent(this, ReminderListActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnBillReminder).setOnClickListener {
            startActivity(Intent(this, BillReminderListActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnExercise).setOnClickListener {
            startActivity(Intent(this, ExerciseCategoryActivity::class.java))
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted, do nothing
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Optionally explain to the user why you need this permission, then request it
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly request the permission
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        // For Android versions below 13, no runtime permission needed for notifications
    }
}

//listOf("bills", "bill reminder").any { spokenText?.contains(it) == true } ->
//speakAndNavigate("Opening bill reminders", BillRemindersActivity::class.java)

//listOf("medicine", "medicine reminder").any { spokenText?.contains(it) == true } ->
//speakAndNavigate("Showing medicine reminders", MedicineReminderActivity::class.java)

//listOf("exercise", "start my workout").any { spokenText?.contains(it) == true } ->
//speakAndNavigate("Opening exercises", ExerciseActivity::class.java)