package com.example.todomvvm.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {


    fun getAllTasks(hideCompleted: Boolean, sortBy: SortBy, searchKey: String): Flow<List<Task>> {
        return when(sortBy) {
            SortBy.SORT_BY_DATE -> {
                getTasksByDateCreated(hideCompleted, searchKey)
            }
            SortBy.SORT_BY_NAME -> {
                getTasksByName(hideCompleted, searchKey)
            }
        }
    }


    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND taskName  LIKE '%' || :searchKey || '%' ORDER BY  created ASC")
    fun getTasksByDateCreated(hideCompleted: Boolean, searchKey: String) :Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND taskName  LIKE '%' || :searchKey || '%' ORDER BY  taskName ASC")
    fun getTasksByName(hideCompleted: Boolean, searchKey: String) :Flow<List<Task>>

    // Add Task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    // Update Task
    @Update
    suspend fun updateTask(task: Task)


    @Delete
    suspend fun deleteTask(task: Task)


}



enum class SortBy {
    SORT_BY_DATE,
    SORT_BY_NAME
}