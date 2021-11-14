package com.example.todomvvm.ui.tasks

import androidx.lifecycle.ViewModel
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TaskFragmentViewModel @Inject constructor(
    taskDao: TaskDao
): ViewModel() {

    val tasks = taskDao.getAllTasks()
}