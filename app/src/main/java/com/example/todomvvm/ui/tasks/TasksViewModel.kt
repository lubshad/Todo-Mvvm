package com.example.todomvvm.ui.tasks


import androidx.lifecycle.*
import com.example.todomvvm.data.PreferenceManger
import com.example.todomvvm.data.Task
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManger: PreferenceManger,
    private val state: SavedStateHandle,
) : ViewModel() {

    val searchQuery = state.getLiveData<String>("searchQuery", "")

    private val taskEventChannel = Channel<TaskEvent>()

    val taskEventFlow = taskEventChannel.receiveAsFlow()


    val preferencesFlow = preferenceManger.preferencesFlow

    @ExperimentalCoroutinesApi
    val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)

    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getAllTasks(query, filterPreferences.sortBy, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun navigateToAddTaskScreen() {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
        }
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

    fun deleteAllCompleted() {

        val completedTasks = tasks.value?.filter { task ->
            task.completed
        }
        if (completedTasks!!.isNotEmpty()) {
            viewModelScope.launch {
                for (task in completedTasks) {
                    taskDao.deleteTask(task)
                }
                taskEventChannel.send(TaskEvent.ShowUndoMultipleTasks(completedTasks))
            }
        }
    }

    fun undoMultipleTasks(tasks: List<Task>) {
        viewModelScope.launch {
            for (task in tasks) {
                taskDao.addTask(task)
            }
        }
    }

    fun navigateToEditTaskFragment(task: Task) {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToEditTask(task))
        }
    }


}

enum class SortBy {
    BY_DATE,
    BY_NAME
}


sealed class TaskEvent {
    data class ShowUndoTaskMessage(val task: Task) : TaskEvent()
    data class ShowUndoMultipleTasks(val tasks: List<Task>) : TaskEvent()
    object NavigateToAddTaskScreen : TaskEvent()
    data class NavigateToEditTask(val task: Task) : TaskEvent()
}