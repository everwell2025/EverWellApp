package com.example.elderlycareapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elderlycareapp.R
import com.example.elderlycareapp.Reminder




class NotificationAdapter(
    private val items: List<NotificationItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        private const val TYPE_MEDICINE = 0
        private const val TYPE_BILL = 1
    }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is NotificationItem.MedicineReminder -> TYPE_MEDICINE
            else -> throw IllegalArgumentException("Unknown notification type at position $position")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MEDICINE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_medicine_notification, parent, false)
                MedicineViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }


    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NotificationItem.MedicineReminder -> (holder as MedicineViewHolder).bind(item)
            else -> throw IllegalArgumentException("Unknown notification type at position $position")
        }
    }


    inner class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tv_title)
        private val tvTime: TextView = view.findViewById(R.id.tv_time)


        fun bind(item: NotificationItem.MedicineReminder) {
            tvTitle.text = "Medicine: ${item.reminder.medicine}"
            tvTime.text = String.format(
                "%02d:%02d on %02d/%02d/%04d",
                item.reminder.hour,
                item.reminder.minute,
                item.reminder.day,
                item.reminder.month,
                item.reminder.year
            )
        }
    }
}
