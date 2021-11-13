package com.example.todomvvm.ui.add_edit_task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomvvm.data.Task
import com.example.todomvvm.data.TaskDao
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle,
) : ViewModel() {


    val addEditTaskChanel = Channel<AddEditTaskEvent>()

    val addEditTaskEvent = addEditTaskChanel.receiveAsFlow()

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var important = state.get<Boolean>("important") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("important", value)
        }

    fun onSaveButtonClick() {
        if (taskName.isBlank()) {
            showEmptyTaskNameMessage()
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = important)
            updateTask(updatedTask)
        }
        else {
            val newTask = Task(name = taskName , important = important)
            addNewTask(newTask)
        }
    }

    private fun showEmptyTaskNameMessage() {
        viewModelScope.launch {
            addEditTaskChanel.send(AddEditTaskEvent.ShowInvalidInputMessage("Name is empty"))
        }
    }

    private fun addNewTask(newTask: Task) {
        viewModelScope.launch {
            taskDao.addTask(newTask)
        }
    }

    private fun updateTask(updatedTask: Task) {
            viewModelScope.launch {
                taskDao.updateTask(updatedTask)
            }
    }

}

sealed class AddEditTaskEvent {
    data class ShowInvalidInputMessage(val message: String): AddEditTaskEvent()

}