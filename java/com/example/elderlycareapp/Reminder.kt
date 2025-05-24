package com.example.elderlycareapp

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar


data class Reminder(
    val medicine: String,
    val hour: Int,
    val minute: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val repeatType: String
) {
    // Check if this reminder is upcoming (date/time >= current)
    fun isUpcoming(): Boolean {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day, hour, minute, 0)
        return cal.timeInMillis >= System.currentTimeMillis()
    }


    // Format reminder date and time as string
    fun formattedDateTime(): String {
        return String.format("%02d/%02d/%d %02d:%02d", day, month, year, hour, minute)
    }


    companion object {


        // Deserialize JSON string to List<Reminder>
        fun fromJsonList(json: String): List<Reminder> {
            val gson = Gson()
            val type = object : TypeToken<List<Reminder>>() {}.type
            return gson.fromJson(json, type)
        }


        // Serialize List<Reminder> to JSON string
        fun toJsonList(reminders: List<Reminder>): String {
            val gson = Gson()
            return gson.toJson(reminders)
        }
    }
}
