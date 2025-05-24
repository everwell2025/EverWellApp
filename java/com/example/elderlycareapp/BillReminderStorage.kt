package com.example.elderlycareapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object BillReminderStorage {


    private const val PREFS_NAME = "reminder_prefs"
    private const val REMINDERS_KEY = "reminders"


    fun saveReminders(context: Context, reminders: List<BillReminder>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(reminders)
        editor.putString(REMINDERS_KEY, json)
        editor.apply()
    }


    fun loadReminders(context: Context): ArrayList<BillReminder> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(REMINDERS_KEY, null)
        if (json == null) return ArrayList()
        val type = object : TypeToken<ArrayList<BillReminder>>() {}.type
        return Gson().fromJson(json, type)
    }
}