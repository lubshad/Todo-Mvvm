package com.example.todomvvm.ui.add_edit_tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomvvm.data.Task
import com.example.todomvvm.data.TaskDao
import com.example.todomvvm.ui.tasks.TASK_ADDED
import com.example.todomvvm.ui.tasks.TASK_EDITED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    state: SavedStateHandle,
) : ViewModel() {




    val task = state.get<Task>("task")

    val taskName = MutableLiveData(task?.taskName ?: "")
    val important = MutableLiveData(task?.important ?: false)


    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()

    val addEditTaskEventFlow = addEditTaskEventChannel.receiveAsFlow()

    private fun addNewTask() {
        val newTask = Task(
            taskName = taskName.value!!,
            important = important.value!!
        )
        viewModelScope.launch {
            taskDao.addTask(newTask)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackToTaskListingScreen(
                TASK_ADDED))
        }
    }

    fun onSaveButtonClick() {
        when {
            taskName.value == "" -> {
                showEmptyTaskMessage()
            }
            task != null -> {
                updateTask()
            }
            else -> {
                addNewTask()
            }
        }
    }

    private fun updateTask() {
        viewModelScope.launch {
            val task = task!!.copy(taskName = taskName.value!!, important = important.value!!)
            taskDao.updateTask(task)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackToTaskListingScreen(
                TASK_EDITED))
        }
    }

    private fun showEmptyTaskMessage() {
        viewModelScope.launch {
            addEditTaskEventChannel.send(AddEditTaskEvent.ShowEmptyTaskMessage)
        }
    }


}


sealed class AddEditTaskEvent {
    object ShowEmptyTaskMessage : AddEditTaskEvent()
    data class NavigateBackToTaskListingScreen(val result: Int) : AddEditTaskEvent()
}