package com.example.elderlycareapp

import android.os.Parcel
import android.os.Parcelable

data class BillReminder(
    val billType: String,
    var date: String,
    val time: String,
    var repeat: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(billType)
        parcel.writeString(date)
        parcel.writeString(time)
        parcel.writeString(repeat)
    }


    override fun describeContents() = 0


    override fun toString(): String {
        return "$billType - $date $time (Repeat: $repeat)"
    }


    companion object CREATOR : Parcelable.Creator<BillReminder> {
        override fun createFromParcel(parcel: Parcel): BillReminder = BillReminder(parcel)
        override fun newArray(size: Int): Array<BillReminder?> = arrayOfNulls(size)
    }
}
