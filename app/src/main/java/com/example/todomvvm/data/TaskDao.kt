package com.example.todomvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :queryString || '%' ")
    fun getAllTasks(queryString: String) : Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}