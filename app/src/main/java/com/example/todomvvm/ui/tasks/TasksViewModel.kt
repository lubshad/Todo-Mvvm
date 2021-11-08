package com.example.todomvvm.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.todomvvm.data.TaskDao
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao
) :ViewModel() {
    val tasks = taskDao.getAllTasks().asLiveData()
}