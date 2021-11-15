package com.example.todomvvm.ui.add_edit_tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todomvvm.data.Task
import com.example.todomvvm.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
) : ViewModel() {

    companion object {
        const val TAG = "AddEditTaskViewModel"
    }

    fun addNewTask() {
        if (taskName.value != "") {
            val newTask = Task(
                taskName = taskName.value!!,
                important = important.value!!
            )
            viewModelScope.launch {
                taskDao.addTask(newTask)
            }
        } else {

        }
    }

    val taskName = MutableLiveData("")
    val important = MutableLiveData(false)


}