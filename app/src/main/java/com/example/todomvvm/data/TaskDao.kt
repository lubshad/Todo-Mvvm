package com.example.todomvvm.data

import androidx.room.*
import com.example.todomvvm.ui.tasks.SortBy
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {


    fun getAllTasks(queryString: String, sortBy: SortBy, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortBy) {
            SortBy.BY_DATE -> getTasksSortedByDate(queryString, hideCompleted)
            SortBy.BY_NAME -> getTasksSortedByName(queryString, hideCompleted)
        }


    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :queryString || '%' ORDER BY important DESC, name")
    fun getTasksSortedByName(queryString: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :queryString || '%' ORDER BY important DESC, createdDate")
    fun getTasksSortedByDate(queryString: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}