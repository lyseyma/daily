package com.kh.daily.widget.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.util.UUID

/**
 * Task data class representing a single task/todo item.
 * This class is shared between the main app and the widget module.
 *
 * Note: Firebase annotations are removed from widget module to avoid GMS security issues.
 * The main app should handle Firebase serialization separately.
 */
data class Task(
    /** Unique identifier for the task */
    var id: String = UUID.randomUUID().toString(),

    /** Title/name of the task */
    var title: String = "",

    /** Detailed description of the task */
    var description: String = "",

    /** Category or tag for grouping tasks */
    var category: String = "",

    /** Due date in string format (ISO format recommended) */
    var dueDate: String = "",

    /** Whether the task has been completed */
    var isCompleted: Boolean = false
) : Parcelable {

    /**
     * Parcelable constructor for Android system serialization
     */
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun describeContents(): Int = 0

    /**
     * Write task data to parcel for system serialization
     */
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
        override fun createFromParcel(parcel: Parcel): Task = Task(parcel)
        override fun newArray(size: Int): Array<Task?> = arrayOfNulls(size)
    }
}