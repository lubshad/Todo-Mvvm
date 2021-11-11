package com.example.todomvvm.ui.tasks


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

import kotlinx.coroutines.flow.flatMapLatest


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortBy = MutableStateFlow(SortBy.BY_DATE)
    val hideCompleted = MutableStateFlow(false)


    val tasksFlow = combine(
        searchQuery,
        sortBy,
        hideCompleted
    ) { query, sort, hideCompleted ->
        Triple(query, sort, hideCompleted)

    }.flatMapLatest { (query, sort, hideCompleted) ->
        taskDao.getAllTasks(query, sort, hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()
}

enum class SortBy {
    BY_DATE,
    BY_NAME
}