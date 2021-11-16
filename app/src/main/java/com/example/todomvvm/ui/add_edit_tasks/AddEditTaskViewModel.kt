package com.example.todomvvm.ui.add_edit_tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomvvm.data.Task
import com.example.todomvvm.data.TaskDao
import com.example.todomvvm.ui.tasks.TaskEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel


@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
) : ViewModel() {

    companion object {
        const val TAG = "AddEditTaskViewModel"
    }


    val addEditTaskEventChannel = Channel<AddEditTaskEvent>()

    val addEditTaskEventFlow = addEditTaskEventChannel.receiveAsFlow()

    fun addNewTask() {
            val newTask = Task(
                taskName = taskName.value!!,
                important = important.value!!
            )
            viewModelScope.launch {
                taskDao.addTask(newTask)
            }
    }

    fun onSaveButtonClick() {
        when {
            taskName.value == "" -> {
                showEmptyTaskMessage()
            }
            else -> {
                addNewTask()
            }

        }
    }

    private fun showEmptyTaskMessage() {

    }

    val taskName = MutableLiveData("")
    val important = MutableLiveData(false)


}


sealed class AddEditTaskEvent {
    object ShowEmptyTaskMessage : AddEditTaskEvent()
    object NavigateBackToTaskListingScreen: AddEditTaskEvent()
}