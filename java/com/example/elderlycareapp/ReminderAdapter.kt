package com.example.elderlycareapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ReminderAdapter(
    private val reminders: List<Reminder>,
    private val onItemClick: (Reminder, Int) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {


    inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_icon)
        val ivEdit: ImageView = view.findViewById(R.id.iv_edit)
        val tvMedicineName: TextView = view.findViewById(R.id.tv_medicine_name)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvRepeat: TextView = view.findViewById(R.id.tv_repeat)
        val cardView: androidx.cardview.widget.CardView = view.findViewById(R.id.card_view)


        init {
            ivEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(reminders[position], position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }


    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.ivIcon.setImageResource(R.drawable.pill)
        holder.tvMedicineName.text = reminder.medicine
        holder.tvTime.text = String.format("%02d:%02d", reminder.hour, reminder.minute)
        holder.tvDate.text = String.format("%02d/%02d/%04d", reminder.day, reminder.month, reminder.year)
        holder.tvRepeat.text = reminder.repeatType


        // Alternate card colors
        val colorRes = if (position % 2 == 0) R.color.card1 else R.color.card2
        holder.cardView.setCardBackgroundColor(holder.itemView.context.getColor(colorRes))
    }


    override fun getItemCount() = reminders.size
}
