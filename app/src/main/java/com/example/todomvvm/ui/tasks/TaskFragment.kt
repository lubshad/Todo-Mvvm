package com.example.todomvvm.ui.tasks

import TaskAdapter
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todomvvm.R
import com.example.todomvvm.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.observeOn


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks) {
    private val viewModel: TasksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskAdapter()


        binding.apply {
            recyclerViewTaskList.adapter = taskAdapter
            recyclerViewTaskList.layoutManager = LinearLayoutManager(requireContext())

        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            taskAdapter.submitList(it)
        }
    }
}