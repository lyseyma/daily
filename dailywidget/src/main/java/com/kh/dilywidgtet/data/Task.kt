package com.kh.dilywidgtet.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class Task(
    @PropertyName("id") var id: String = UUID.randomUUID().toString(),
    @PropertyName("title") var title: String = "",
    @PropertyName("description") var description: String = "",
    @PropertyName("category") var category: String = "",
    @PropertyName("dueDate") var dueDate: String = "",
    @PropertyName("isCompleted") var isCompleted: Boolean = false
    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    )

    override fun describeContents(): Int {
        return 0
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(category)
        dest.writeString(dueDate)
        dest.writeBoolean(isCompleted)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}