package com.example.todomvvm.ui.tasks

import TaskAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todomvvm.R
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.FragmentTasksBinding
import com.example.todomvvm.utils.onQueryChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnClickListener {
    private val viewModel: TasksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskAdapter(this)


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
                                .setAction("UNDO", View.OnClickListener {
                                    viewModel.undoDeletedTask(event.task)
                                })
                                .show()
                        }
                        is TaskEvent.ShowUndoMultipleTasks -> {
                            Snackbar.make(requireView(),
                                "Multiple task Deleted",
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction("UNDO", View.OnClickListener {
                                    viewModel.undoMultipleTasks(event.tasks)
                                })
                                .show()
                        }
                    }
                }
            }
        }





        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)


        val searchButton = menu.findItem(R.id.action_search)
        val searchView = searchButton.actionView as SearchView

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
                viewModel.deleteAllCompleted()
            }
        }
        return true
    }

    override fun onItemClick(task: Task) {

    }

    override fun onCheckboxClick(task: Task) {
        viewModel.changeCheckedStatus(task)
    }
}