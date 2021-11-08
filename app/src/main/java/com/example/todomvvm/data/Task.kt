package com.example.todomvvm.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat


@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val important:Boolean = false,
    val completed:Boolean = false,
    val createdDate: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id:Int = 0
) : Parcelable {
    val createdDateFormated: String get() = DateFormat.getDateTimeInstance().format(createdDate)
}
