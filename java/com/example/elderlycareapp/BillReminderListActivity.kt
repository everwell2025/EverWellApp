package com.example.elderlycareapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import com.example.elderlycareapp.BillReminderAdapter


class BillReminderListActivity : BaseActivity() {


    private lateinit var listViewReminders: ListView
    private lateinit var reminders: ArrayList<BillReminder>
    private lateinit var adapter: BillReminderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bill_activity_reminder_list)


        listViewReminders = findViewById(R.id.listViewReminders)

        setupBottomNavigation(null)

        reminders = BillReminderStorage.loadReminders(this)


        adapter = BillReminderAdapter(this, reminders)
        listViewReminders.adapter = adapter


        // Show dialog if launched from notification with specific reminder
        val billType = intent.getStringExtra("billType")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val repeat = intent.getStringExtra("repeat")


        if (billType != null && date != null && time != null) {
            showReminderPopup(billType, date, time, repeat ?: "Never")
        }


        listViewReminders.setOnItemClickListener { parent, view, position, id ->
            val reminder = reminders[position]


            AlertDialog.Builder(this)
                .setTitle("Payment Confirmation")
                .setMessage("Have you paid ${reminder.billType}?")
                .setPositiveButton("Yes") { dialog, _ ->
                    if (reminder.repeat == "Never") {
                        // Delete the reminder if not repeating
                        reminders.removeAt(position)
                        Toast.makeText(this, "${reminder.billType} reminder deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update the due date for repeating reminders
                        reminder.date = getNextDueDate(reminder)
                        Toast.makeText(this, "${reminder.billType} reminder updated to next due date: ${reminder.date}", Toast.LENGTH_SHORT).show()
                    }
                    adapter.notifyDataSetChanged()
                    BillReminderStorage.saveReminders(this, reminders)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Update the due date to tomorrow for "No" answer and notify user
                    reminder.date = getTomorrowDate()
                    adapter.notifyDataSetChanged()
                    BillReminderStorage.saveReminders(this, reminders)
                    Toast.makeText(this, "You will be notified tomorrow for ${reminder.billType}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .show()
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }


    private fun showReminderPopup(billType: String, date: String, time: String, repeat: String) {
        AlertDialog.Builder(this)
            .setTitle("Did you pay the $billType?")
            .setPositiveButton("Yes, Bill is Paid") { dialog, _ ->
                dialog.dismiss()
                showRepeatDialog(billType, date, time, repeat)
            }
            .setNegativeButton("No, Bill is not Paid Yet") { dialog, _ ->
                dialog.dismiss()
                snoozeReminder(billType, date, time, repeat)
                Toast.makeText(this, "You will be reminded again tomorrow at the same time", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }


    private fun showRepeatDialog(billType: String, date: String, time: String, repeat: String) {
        val options = arrayOf("Repeat next week", "Repeat next month", "Delete reminder")


        AlertDialog.Builder(this)
            .setTitle("Would you like to repeat this reminder?")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> updateReminderRepeat(billType, date, time, "Weekly")
                    1 -> updateReminderRepeat(billType, date, time, "Monthly")
                    2 -> deleteReminder(billType, date, time)
                }
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }


    private fun updateReminderRepeat(billType: String, date: String, time: String, newRepeat: String) {
        // Find the reminder, update repeat & save
        val reminder = reminders.find { it.billType == billType && it.date == date && it.time == time }
        if (reminder != null) {
            reminder.repeat = newRepeat
            // You may want to update the date/time for next occurrence here if needed
            // For example, add 7 days for Weekly or 1 month for Monthly
            adapter.notifyDataSetChanged()
            BillReminderStorage.saveReminders(this, reminders)
            Toast.makeText(this, "Reminder set to repeat $newRepeat", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Reminder not found.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteReminder(billType: String, date: String, time: String) {
        val removed = reminders.removeIf { it.billType == billType && it.date == date && it.time == time }
        if (removed) {
            adapter.notifyDataSetChanged()
            BillReminderStorage.saveReminders(this, reminders)
            Toast.makeText(this, "Reminder deleted.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Reminder not found.", Toast.LENGTH_SHORT).show()
        }
    }




    @SuppressLint("ScheduleExactAlarm", "DefaultLocale")
    private fun snoozeReminder(billType: String, date: String, time: String, repeat: String) {
        Log.d("ReminderDebug", "Incoming billType: $billType")
        Log.d("ReminderDebug", "Incoming date: $date")
        Log.d("ReminderDebug", "Incoming time: $time")


        try {
            val dateParts = date.split("/")
            val timeParts = time.split(":")


            if (dateParts.size != 3 || timeParts.size != 2) {
                Toast.makeText(this, "Invalid date or time format", Toast.LENGTH_SHORT).show()
                return
            }


            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, dateParts[2].toInt())
            calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)


            // Add 1 day
            calendar.add(Calendar.DAY_OF_MONTH, 1)


            val newDate = String.format(
                "%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )


            val newTime = String.format(
                "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )


            // Remove old reminder
            val removed = reminders.removeIf { it.billType == billType && it.date == date && it.time == time }
            if (removed) {
                adapter.notifyDataSetChanged()
            }


            // Add new one
            val newReminder = BillReminder(billType, newDate, newTime, repeat)
            reminders.add(newReminder)
            BillReminderStorage.saveReminders(this, reminders)
            adapter.notifyDataSetChanged()


            val intent = Intent(this, BillReminderBroadcast::class.java).apply {
                putExtra("billType", billType)
                putExtra("date", newDate)
                putExtra("time", newTime)
                putExtra("repeat", repeat)
            }


            val pendingIntent = PendingIntent.getBroadcast(
                this,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )


            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )


            Toast.makeText(this, "You will be reminded again tomorrow at the same time", Toast.LENGTH_LONG).show()


        } catch (e: Exception) {
            Toast.makeText(this, "Error snoozing reminder: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getNextDueDate(reminder: BillReminder): String {
        val parts = reminder.date.split("/")
        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1  // Calendar month is 0-based
        val year = parts[2].toInt()


        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)


        when (reminder.repeat) {
            "Daily" -> calendar.add(Calendar.DAY_OF_MONTH, 1)
            "Weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "Monthly" -> calendar.add(Calendar.MONTH, 1)
            // Add other repeat intervals if you want
        }


        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    }


    private fun getTomorrowDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
    }
}
