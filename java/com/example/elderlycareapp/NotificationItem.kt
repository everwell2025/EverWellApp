package com.example.elderlycareapp

sealed class NotificationItem {
    data class MedicineReminder(val reminder: Reminder) : NotificationItem()
    // Leave space for future BillReminder
    // data class BillReminder(val bill: Bill) : NotificationItem()
}
