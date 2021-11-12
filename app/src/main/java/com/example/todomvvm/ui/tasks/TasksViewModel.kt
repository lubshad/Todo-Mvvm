package com.example.todomvvm.ui.tasks


import androidx.lifecycle.*
import com.example.todomvvm.data.PreferenceManger
import com.example.todomvvm.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManger: PreferenceManger
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val taskEventChannel = Channel<TaskEvent>()

    val taskEventFlow = taskEventChannel.receiveAsFlow()


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

    fun changeCheckedStatus(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(completed = !task.completed))
        }
    }

    fun onTaskSwiped(task: Task) {
          viewModelScope.launch {
              taskDao.deleteTask(task)
              taskEventChannel.send(TaskEvent.ShowUndoTaskMessage(task))
          }
    }

    fun undoDeletedTask(task: Task) {
        viewModelScope.launch {
            taskDao.addTask(task)
        }
    }

    val tasks = tasksFlow.asLiveData()
}

enum class SortBy {
    BY_DATE,
    BY_NAME
}


sealed class TaskEvent {
    data class ShowUndoTaskMessage(val task:Task) : TaskEvent()
}