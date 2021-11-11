package com.example.todomvvm.ui.tasks


import androidx.lifecycle.*
import com.example.todomvvm.data.PreferenceManger
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManger: PreferenceManger
) : ViewModel() {

    val searchQuery = MutableStateFlow("")


    val preferencesFlow = preferenceManger.preferencesFlow

    val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)

    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getAllTasks(query, filterPreferences.sortBy, filterPreferences.hideCompleted)
    }

    fun changeSortOrder(sortOrder: SortBy) {
        viewModelScope.launch {
            preferenceManger.updateSortOrder(sortOrder)
        }
    }

    fun changeHideCompleted(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferenceManger.updateHideCompleted(hideCompleted)
        }
    }

    val tasks = tasksFlow.asLiveData()
}

enum class SortBy {
    BY_DATE,
    BY_NAME
}