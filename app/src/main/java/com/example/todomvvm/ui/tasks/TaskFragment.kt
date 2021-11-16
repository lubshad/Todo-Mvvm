package com.example.todomvvm.ui.tasks

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todomvvm.R
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.FragmentTasksBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


const val ADD_EDIT_TASK_REQUEST = "add_edit_task_request"

const val TASK_ADDED = Activity.RESULT_FIRST_USER
const val TASK_EDITED = TASK_ADDED + 1

@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnItemClickListener {

    private val viewModel: TaskFragmentViewModel by viewModels()

    private lateinit var binding: FragmentTasksBinding
    private lateinit var tasksAdapter: TasksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTasksBinding.bind(view)
        tasksAdapter = TasksAdapter(this)
        binding.apply {

            recyclerViewTasks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = tasksAdapter
            }


            fabAddTask.setOnClickListener {
                viewModel.navigateToAddTaskScreen()
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.taskEventFlow.collect { event ->
                when (event) {
                    is TaskEvent.NavigateToAddTaskScreen -> {
                        val action =
                            TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment()
                        findNavController().navigate(action)
                    }
                    is TaskEvent.ShowTaskEventMessage -> {
                        val message = event.message
                        showTaskEventMessage(message, requireView())
                    }
                    is TaskEvent.NavigateToEditTaskScreen -> {
                        navigateToEditTaskScreen(event.task)
                    }
                }
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }


        setFragmentResultListener(ADD_EDIT_TASK_REQUEST) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_TASK_REQUEST)
            viewModel.onAddEditTaskResult(result)
        }

    }

    private fun navigateToEditTaskScreen(task: Task) {
        val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(task = task)
        findNavController().navigate(action)
    }

    private fun showTaskEventMessage(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onItemClick(task: Task) {
        viewModel.onItemClick(task)
    }

    override fun onCheckboxClick(task: Task, value: Boolean) {
        viewModel.onCheckBoxClick(task, value)
    }

}