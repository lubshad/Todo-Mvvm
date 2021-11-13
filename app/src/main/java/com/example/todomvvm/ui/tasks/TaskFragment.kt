package com.example.todomvvm.ui.tasks

import TaskAdapter
import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todomvvm.R
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.FragmentTasksBinding
import com.example.todomvvm.utils.onQueryChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


const val TASK_ADDED = Activity.RESULT_FIRST_USER
const val TASK_EDITED = TASK_ADDED + 1
const val DELETE_ALL_COMPLETED = TASK_EDITED + 1

const val ADD_EDIT_RESULT_KEY = "add_edit_result_key"
const val DELETE_ALL_COMPLETED_RESULT_KEY = "delete_all_completed_result_key"


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnClickListener {
    private val viewModel: TasksViewModel by viewModels()

    private lateinit var searchView : SearchView


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskAdapter(this)


        setFragmentResultListener(ADD_EDIT_RESULT_KEY) { _, bundle ->
            val result = bundle.get(ADD_EDIT_RESULT_KEY) as Int
            viewModel.showAddEditTaskMessage(result)
//            val message = if (result == TASK_ADDED) "Task Added" else "Task Updated"
//            showMessage(message, requireView())
        }

        setFragmentResultListener(DELETE_ALL_COMPLETED_RESULT_KEY) { _, bundle ->
            val result = bundle.get(DELETE_ALL_COMPLETED_RESULT_KEY) as Int
            if (result == DELETE_ALL_COMPLETED) {
                viewModel.deleteAllCompleted()
            }
        }



        binding.apply {
            recyclerViewTaskList.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    if (direction == ItemTouchHelper.LEFT) {
                    val position = viewHolder.adapterPosition
                    val task = taskAdapter.currentList[position]
                    viewModel.onTaskSwiped(task)
//                    }
                }

            }).attachToRecyclerView(recyclerViewTaskList)


            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.taskEventFlow.collect { event ->
                    when (event) {
                        is TaskEvent.ShowUndoTaskMessage -> {
                            Snackbar.make(requireView(), "Task Deleted", Snackbar.LENGTH_INDEFINITE)
                                .setAction("UNDO") {
                                    viewModel.undoDeletedTask(event.task)
                                }.show()

                        }
                        is TaskEvent.ShowUndoMultipleTasks -> {
                            val message =
                                if (event.tasks.size > 1) "Multiple task Deleted" else "Task Deleted"
                            Snackbar.make(requireView(),
                                message,
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction("UNDO") {
                                    viewModel.undoMultipleTasks(event.tasks)
                                }
                                .show()
                        }
                        TaskEvent.NavigateToAddTaskScreen -> {
                            val action =
                                TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment()
                            findNavController().navigate(action)
                        }
                        is TaskEvent.NavigateToEditTask -> {
                            val action =
                                TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(task = event.task,
                                    title = "Edit Task")
                            findNavController().navigate(action)
                        }
                        is TaskEvent.ShowAddEditTaskMessage -> {
                            showMessage(event.message, requireView())
                        }
                        TaskEvent.ShowConfirmDeleteAllDialog -> {
                            val action = TaskFragmentDirections.actionGlobalConfirmDialog()
                            findNavController().navigate(action)
                        }
                    }
                }
            }


            fabAddTask.setOnClickListener {
                viewModel.navigateToAddTaskScreen()
            }
        }





        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }
    }

    private fun showMessage(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)


        val searchButton = menu.findItem(R.id.action_search)
        searchView = searchButton.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotBlank()) {
            searchButton.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryChanged {
            viewModel.searchQuery.value = it
        }


        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_by_date -> {
                viewModel.changeSortOrder(SortBy.BY_DATE)
            }
            R.id.action_sort_by_name -> {
                viewModel.changeSortOrder(SortBy.BY_NAME)
            }
            R.id.action_hide_completed -> {
                item.isChecked = !item.isChecked
                viewModel.changeHideCompleted(item.isChecked)
            }
            R.id.action_delete_completed -> {
                viewModel.showConfirmDeleteAllDialog()
            }
        }
        return true
    }

    override fun onItemClick(task: Task) {
        viewModel.navigateToEditTaskFragment(task)
    }

    override fun onCheckboxClick(task: Task) {
        viewModel.changeCheckedStatus(task)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}