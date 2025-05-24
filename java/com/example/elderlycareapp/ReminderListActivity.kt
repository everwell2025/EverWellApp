package com.example.elderlycareapp


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ReminderListActivity : BaseActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var rvReminders: RecyclerView
    private lateinit var reminderList: MutableList<Reminder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder_list)
        setupBottomNavigation(null)

        rvReminders = findViewById(R.id.rv_reminders)
        reminderList = loadReminders().toMutableList()


        reminderAdapter = ReminderAdapter(reminderList) { clickedReminder, position ->
            showEditDeleteDialog(clickedReminder, position)
        }


        rvReminders.layoutManager = LinearLayoutManager(this)
        rvReminders.adapter = reminderAdapter


        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { v: View? ->
            finish() // Goes back to previous activity
        }
    }


    private fun loadReminders(): List<Reminder> {
        val sharedPreferences = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val remindersJson = sharedPreferences.getString("reminders", "[]")
        return Reminder.fromJsonList(remindersJson!!)
    }


    private fun saveReminders() {
        val sharedPreferences = getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("reminders", Reminder.toJsonList(reminderList))
        editor.apply()
    }


    private fun showEditDeleteDialog(reminder: Reminder, position: Int) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(this)
            .setTitle("Choose an action for '${reminder.medicine}'")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(reminder, position)
                    1 -> deleteReminder(position)
                }
            }
            .show()
    }


    private fun deleteReminder(position: Int) {
        reminderList.removeAt(position)
        reminderAdapter.notifyItemRemoved(position)
        saveReminders()
        Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show()
    }


    private fun showEditDialog(reminder: Reminder, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_add_reminder, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()


        val etMedicine = dialogView.findViewById<EditText>(R.id.et_medicine)
        val btnDate = dialogView.findViewById<Button>(R.id.btn_date)
        val btnTime = dialogView.findViewById<Button>(R.id.btn_time)
        val spinnerRepeat = dialogView.findViewById<Spinner>(R.id.spinner_repeat)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)


        val repeatOptions = arrayOf("Once", "Daily", "Weekly", "Monthly")
        spinnerRepeat.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, repeatOptions)


        // Pre-fill data
        etMedicine.setText(reminder.medicine)
        btnDate.text = "${reminder.day}/${reminder.month}/${reminder.year}"
        btnTime.text = String.format("%02d:%02d", reminder.hour, reminder.minute)
        spinnerRepeat.setSelection(repeatOptions.indexOf(reminder.repeatType))


        var selectedDay = reminder.day
        var selectedMonth = reminder.month
        var selectedYear = reminder.year
        var selectedHour = reminder.hour
        var selectedMinute = reminder.minute


        btnDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                selectedDay = day
                selectedMonth = month + 1
                selectedYear = year
                btnDate.text = "$day/${month + 1}/$year"
            }, reminder.year, reminder.month - 1, reminder.day).show()
        }


        btnTime.setOnClickListener {
            TimePickerDialog(this, { _, hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                btnTime.text = String.format("%02d:%02d", hour, minute)
            }, reminder.hour, reminder.minute, false).show()
        }


        btnSave.setOnClickListener {
            val updatedReminder = Reminder(
                medicine = etMedicine.text.toString().trim(),
                day = selectedDay,
                month = selectedMonth,
                year = selectedYear,
                hour = selectedHour,
                minute = selectedMinute,
                repeatType = spinnerRepeat.selectedItem.toString()
            )


            reminderList[position] = updatedReminder
            reminderAdapter.notifyItemChanged(position)
            saveReminders()
            Toast.makeText(this, "Reminder updated", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }


        btnCancel.setOnClickListener { dialog.dismiss() }


        dialog.show()
    }


}
