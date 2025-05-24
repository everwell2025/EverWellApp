package com.example.elderlycareapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import java.util.*
import android.provider.Settings
import android.net.Uri
import android.view.View


class BillReminderActivity : BaseActivity() {


    private lateinit var spinnerBillType: Spinner
    private lateinit var btnSetDate: Button
    private lateinit var btnSetTime: Button
    private lateinit var btnSaveReminder: Button
    private lateinit var btnViewReminders: Button
    private lateinit var radioRepeat: RadioGroup


    private var selectedDate = ""
    private var selectedTime = ""
    private var reminderList = ArrayList<BillReminder>()


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bill_activity_reminder)
        createNotificationChannel()
        setupBottomNavigation(null)

        spinnerBillType = findViewById(R.id.spinnerBillType)
        btnSetDate = findViewById(R.id.btnSetDate)
        btnSetTime = findViewById(R.id.btnSetTime)
        btnSaveReminder = findViewById(R.id.btnSaveReminder)
        btnViewReminders = findViewById(R.id.btnViewReminders)
        radioRepeat = findViewById(R.id.radioRepeat)


        ArrayAdapter.createFromResource(
            this,
            R.array.bill_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerBillType.adapter = adapter
        }


        btnSetDate.setOnClickListener {
            val datePicker = DatePickerFragment { year, month, day ->
                selectedDate = "$day/${month + 1}/$year"
                Toast.makeText(this, "Date selected: $selectedDate", Toast.LENGTH_SHORT).show()
            }
            datePicker.show(supportFragmentManager, "datePicker")
        }


        btnSetTime.setOnClickListener {
            val timePicker = TimePickerFragment { hour, minute ->
                selectedTime = String.format("%02d:%02d", hour, minute)
                Toast.makeText(this, "Time selected: $selectedTime", Toast.LENGTH_SHORT).show()
            }
            timePicker.show(supportFragmentManager, "timePicker")
        }

        btnSaveReminder.setOnClickListener {
            saveReminder()
        }

        btnViewReminders.setOnClickListener {
            val intent = Intent(this, BillReminderListActivity::class.java)
            intent.putParcelableArrayListExtra("reminders", reminderList)
            startActivity(intent)
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun saveReminder() {
        if (spinnerBillType.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select a bill type", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }


        val billType = spinnerBillType.selectedItem.toString()
        val repeat = when (radioRepeat.checkedRadioButtonId) {
            R.id.radioWeekly -> "Weekly"
            R.id.radioMonthly -> "Monthly"
            else -> "Never"
        }


        val reminder = BillReminder(billType, selectedDate, selectedTime, repeat)
        reminderList.add(reminder)
        BillReminderStorage.saveReminders(this, reminderList)




        scheduleNotification(reminder)
    }


    @SuppressLint("ObsoleteSdkInt")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun scheduleNotification(reminder: BillReminder) {
        val calendar = Calendar.getInstance()


        val dateParts = reminder.date.split("/")
        val timeParts = reminder.time.split(":")


        val day = dateParts[0].toInt()
        val month = dateParts[1].toInt() - 1
        val year = dateParts[2].toInt()


        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()


        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)


        val now = System.currentTimeMillis()
        val buffer = 5000L


        if (calendar.timeInMillis <= now + buffer) {
            Toast.makeText(this, "Selected time is too close or in the past. Please pick a later time.", Toast.LENGTH_SHORT).show()
            return
        }


        val intent = Intent(this, BillReminderBroadcast::class.java).apply {
            putExtra("billType", reminder.billType)
        }


        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                return
            }
        }


        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )


        Toast.makeText(this, "Reminder scheduled!", Toast.LENGTH_SHORT).show()
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ReminderChannel"
            val descriptionText = "Channel for bill reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("reminderChannel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}