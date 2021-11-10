package com.example.todomvvm.ui.tasks


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val tasksFlow = searchQuery.flatMapLatest {
        taskDao.getAllTasks(it)
    }

    val tasks = tasksFlow.asLiveData()
}