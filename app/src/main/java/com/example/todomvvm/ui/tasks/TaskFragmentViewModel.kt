package com.example.todomvvm.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import com.example.todomvvm.ApplicationScope
import com.example.todomvvm.data.SortBy
import com.example.todomvvm.data.Task
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskFragmentViewModel @Inject constructor(
    val taskDao: TaskDao,
    @ApplicationScope val applicationScope: CoroutineScope,
) : ViewModel() {

    companion object {
        const val TAG = "TaskFragmentViewModel"
    }

    val hideCompleted = MutableLiveData(false)

    val searchKey = MutableLiveData("")

    private val sortBy = MutableLiveData(SortBy.SORT_BY_DATE)


    fun navigateToAddTaskScreen() {
        viewModelScope.launch {
            taskEventChanel.send(TaskEvent.NavigateToAddTaskScreen)
        }
    }

    fun onAddEditTaskResult(result: Int) {
        when (result) {
            TASK_ADDED -> {
                val message = "Task Added"
                showTaskEventMessage(message)
            }
            TASK_EDITED -> {
                val message = "Task Edited"
                showTaskEventMessage(message)
            }
        }
    }

    private fun showTaskEventMessage(message: String) {
        viewModelScope.launch {
            taskEventChanel.send(TaskEvent.ShowTaskEventMessage(message))
        }

    }

    fun onItemClick(task: Task) {
        viewModelScope.launch {
            taskEventChanel.send(TaskEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onCheckBoxClick(task: Task, value: Boolean) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(completed = value))
        }
    }

    fun sortByDate() {
        sortBy.value = SortBy.SORT_BY_DATE
        Log.i(TAG, "Sort By Date")
    }

    fun sortByName() {
        sortBy.value = SortBy.SORT_BY_NAME
        Log.i(TAG, "Sort By Name")
    }

    fun hideCompleted() {
        hideCompleted.value = !hideCompleted.value!!
    }

    fun deleteCompleted() {
        val completedTasks = tasks.value!!.filter { task -> task.completed }
        if (completedTasks.isNotEmpty()) {

        applicationScope.launch {
            for (task in completedTasks) {
                taskDao.deleteTask(task)
            }
        }
            showUndoDeletedMessage(completedTasks)
        }
    }

    private fun showUndoDeletedMessage(deletedTasks: List<Task>) {
        viewModelScope.launch {
            taskEventChanel.send(TaskEvent.ShowUndoDeletedMessage(deletedTasks))
        }
    }

    fun undoDeletedTasks(deletedTasks: List<Task>) {
        viewModelScope.launch {
            for (task in deletedTasks) {
                taskDao.addTask(task)
            }
        }
    }

    fun deleteTask(task: Task?) {
        viewModelScope.launch {
            taskDao.deleteTask(task!!)
            showUndoDeletedMessage(deletedTasks = listOf(task))
        }
    }


    private val taskFlow = combine(
        searchKey.asFlow(), sortBy.asFlow(), hideCompleted.asFlow()
    ) { searchKey, sortBy, hideCompleted ->
        Triple(searchKey, sortBy, hideCompleted)
    }.flatMapLatest { (searchKey, sortBy, hideCompleted) ->
        taskDao.getAllTasks(hideCompleted, sortBy, searchKey)
    }



    val tasks = taskFlow.asLiveData()

    private val taskEventChanel = Channel<TaskEvent>()

    val taskEventFlow = taskEventChanel.receiveAsFlow()
}

sealed class TaskEvent {
    object NavigateToAddTaskScreen : TaskEvent()
    data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
    data class ShowTaskEventMessage(val message: String) : TaskEvent()
    data class ShowUndoDeletedMessage(val deletedTasks: List<Task>) : TaskEvent()
}
