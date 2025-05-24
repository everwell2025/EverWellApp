package com.example.elderlycareapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageView

class BillReminderAdapter(private val context: Context, private val reminders: ArrayList<BillReminder>) : BaseAdapter() {


    override fun getCount(): Int = reminders.size


    override fun getItem(position: Int): Any = reminders[position]


    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.bill_item_reminder_card, parent, false)


        val billType = view.findViewById<TextView>(R.id.textBillName)
        val dueDate = view.findViewById<TextView>(R.id.textDueDate)


        val reminder = reminders[position]
        billType.text = reminder.billType
        dueDate.text = "Due on: ${reminder.date} at ${reminder.time}"


        // Set shape background
        view.background = context.getDrawable(R.drawable.bill_card_background)


        // Alternate background tint color
        val tintColor = if (position % 2 == 0)
            context.getColor(R.color.pink)   // Even
        else
            context.getColor(R.color.yellow) // Odd


        view.background.setTint(tintColor)


        return view
    }
}