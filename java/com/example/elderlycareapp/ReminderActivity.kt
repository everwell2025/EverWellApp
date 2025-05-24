package com.example.elderlycareapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class ReminderActivity : BaseActivity() {


    private lateinit var etMedicineName: EditText
    private lateinit var btnSetDate: Button
    private lateinit var btnSetTime: Button
    private lateinit var btnSaveReminder: Button
    private lateinit var btnMic: ImageButton
    private lateinit var btnViewReminders: Button
    private lateinit var rgRepeatOptions: RadioGroup


    private var selectedCalendar: Calendar? = null
    private var repeatOption: String = "Never"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        setupBottomNavigation(null)

        etMedicineName = findViewById(R.id.et_medicine_name)
        btnSetDate = findViewById(R.id.btn_set_date)
        btnSetTime = findViewById(R.id.btn_set_time)
        btnSaveReminder = findViewById(R.id.btn_save_reminder)
        btnMic = findViewById(R.id.btn_mic)
        btnViewReminders = findViewById(R.id.btn_view_reminders)
        rgRepeatOptions = findViewById(R.id.rg_repeat_options)


        btnSetDate.setOnClickListener { showDatePicker() }
        btnSetTime.setOnClickListener { showTimePicker() }
        btnSaveReminder.setOnClickListener { saveReminder() }
        btnMic.setOnClickListener { startSpeechToText() }
        btnViewReminders.setOnClickListener {
            val intent = Intent(this, ReminderListActivity::class.java)
            startActivity(intent)
        }

        // Default selection
        rgRepeatOptions.check(R.id.rb_never)

        // Set repeat option based on selected radio button
        rgRepeatOptions.setOnCheckedChangeListener { _, checkedId ->
            repeatOption = when (checkedId) {
                R.id.rb_daily -> "Daily"
                R.id.rb_weekly -> "Weekly"
                else -> "Never"
            }
        }

        createNotificationChannel()

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            if (selectedCalendar == null) {
                selectedCalendar = Calendar.getInstance()
            }
            selectedCalendar?.set(year, month, dayOfMonth)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }


    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            if (selectedCalendar == null) {
                selectedCalendar = Calendar.getInstance()
            }
            selectedCalendar?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedCalendar?.set(Calendar.MINUTE, minute)
            selectedCalendar?.set(Calendar.SECOND, 0)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
    }


    private fun saveReminder() {
        val medicineName = etMedicineName.text.toString().trim()


        if (medicineName.isEmpty() || selectedCalendar == null) {
            Toast.makeText(this, "Please enter medicine name and select date/time", Toast.LENGTH_SHORT).show()
            return
        }


        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("medicineName", medicineName)
        }


        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        selectedCalendar?.let {
            if (it.timeInMillis > System.currentTimeMillis()) {
                when (repeatOption) {
                    "Daily" -> alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        it.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                    "Weekly" -> alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        it.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent
                    )
                    else -> alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        it.timeInMillis,
                        pendingIntent
                    )
                }


                Toast.makeText(this, "Reminder set for $medicineName ($repeatOption)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please select a future time", Toast.LENGTH_SHORT).show()
                return
            }
        }


        // Save reminder data to SharedPreferences
        val calendar = selectedCalendar!!
        val reminder = Reminder(
            medicine = medicineName,
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE),
            day = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH) + 1,
            year = calendar.get(Calendar.YEAR),
            repeatType = repeatOption
        )


        val sharedPreferences = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val existingJson = sharedPreferences.getString("reminders", "[]")
        val existingReminders = Reminder.fromJsonList(existingJson!!).toMutableList()


        existingReminders.add(reminder)
        val updatedJson = Reminder.toJsonList(existingReminders)
        sharedPreferences.edit().putString("reminders", updatedJson).apply()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "REMINDER_CHANNEL_ID",
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val spokenText = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spokenText.isNullOrEmpty()) {
                etMedicineName.setText(spokenText[0])
            }
        }
    }


    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())


        speechLauncher.launch(intent)
    }
}
