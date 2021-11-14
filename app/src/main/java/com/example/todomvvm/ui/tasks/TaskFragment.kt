package com.example.todomvvm.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todomvvm.R
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TaskFragmentViewModel by viewModels()

    private lateinit var binding: FragmentTasksBinding
    private lateinit var tasksAdapter: TasksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTasksBinding.bind(view)
        tasksAdapter = TasksAdapter()
        binding.apply {

            recyclerViewTasks.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = tasksAdapter
            }




            fabAddTask.setOnClickListener {
                val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment()
                findNavController().navigate(action)
            }
        }





    }

}