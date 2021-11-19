package com.example.todomvvm.ui.tasks

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
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todomvvm.R
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.FragmentTasksBinding
import com.example.todomvvm.utils.onQueryTextChanged
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
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding = FragmentTasksBinding.bind(view)
        tasksAdapter = TasksAdapter(this)
        binding.apply {

            recyclerViewTasks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = tasksAdapter
            }


            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val task = tasksAdapter.currentList[position]
                    viewModel.deleteTask(task)
                }

            }).attachToRecyclerView(recyclerViewTasks)


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
                    is TaskEvent.ShowUndoDeletedMessage -> {
                        showUndoDeletedMessage(event.deletedTasks)
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
//    override fun onDestroyView() {
//        super.onDestroyView()
//        searchView.setOnQueryTextListener(null)
//    }

    private fun showUndoDeletedMessage(deletedTasks: List<Task>) {
        val message = if (deletedTasks.size > 1) "Multiple Task Deleted" else "Task Deleted"
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                viewModel.undoDeletedTasks(deletedTasks)
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_tasks_menu, menu)
        viewModel.hideCompleted.observe(viewLifecycleOwner) {
            val checkboxHideCompleted = menu.findItem(R.id.action_hide_all_completed)
            checkboxHideCompleted.isChecked = viewModel.hideCompleted.value!!
        }
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchKey.value

        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged { query ->
            viewModel.searchKey.value = query
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_by_date -> {
                viewModel.sortByDate()
            }
            R.id.action_sort_by_name -> {
                viewModel.sortByName()
            }
            R.id.action_hide_all_completed -> {
                viewModel.hideCompleted()
            }
            R.id.action_delete_all_completed -> {
                viewModel.deleteCompleted()
            }
        }
        return true
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