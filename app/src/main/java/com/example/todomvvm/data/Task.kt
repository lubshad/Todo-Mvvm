package com.example.todomvvm.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task_table")
data class Task(
    val taskName: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    @PrimaryKey
    val id: Int = 0,
)
