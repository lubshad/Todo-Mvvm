package com.example.todomvvm.ui.tasks


import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {
    val tasks = taskDao.getAllTasks().asLiveData()
}