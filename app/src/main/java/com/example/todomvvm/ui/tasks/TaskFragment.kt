package com.example.todomvvm.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todomvvm.R
import com.example.todomvvm.databinding.FragmentTasksBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TaskFragment : Fragment(R.layout.fragment_tasks) {

    val viewModel : TaskFragmentViewModel by viewModels()

    private lateinit var binding: FragmentTasksBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTasksBinding.bind(view)
        binding.apply {
            fabAddTask.setOnClickListener {
                val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment()
                findNavController().navigate(action)
            }
        }
    }

}