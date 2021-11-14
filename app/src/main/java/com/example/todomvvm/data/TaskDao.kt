package com.example.todomvvm.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    //Get all Tasks
    @Query("SELECT * FROM task_table")
    fun getAllTasks() : Flow<List<Task>>

    // Add Task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    // Update Task
    @Update
    suspend fun updateTask(task:Task)


    @Delete
    suspend fun deleteTask(task: Task)



}