package com.example.todomvvm.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskFragmentViewModel @Inject constructor(
    taskDao: TaskDao,
) : ViewModel() {
    fun navigateToAddTaskScreen() {
        viewModelScope.launch {
            taskEventChanel.send(TaskEvent.NavigateToAddTaskScreen)
        }
    }

    val tasks = taskDao.getAllTasks().asLiveData()

    private val taskEventChanel = Channel<TaskEvent>()

    val taskEventFlow = taskEventChanel.receiveAsFlow()
}

sealed class TaskEvent {
    object NavigateToAddTaskScreen : TaskEvent()
}
