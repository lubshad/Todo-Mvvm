package com.example.todomvvm.ui.add_edit_tasks

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todomvvm.R
import com.example.todomvvm.databinding.FragmentAddEditTaskBinding
import com.example.todomvvm.ui.add_edit_tasks.AddEditTaskEvent.NavigateBackToTaskListingScreen
import com.example.todomvvm.ui.tasks.ADD_EDIT_TASK_REQUEST
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {


    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            textTaskName.apply {
                setText(viewModel.taskName.value)
                addTextChangedListener {
                    viewModel.taskName.value = it.toString()
                }
            }
            textCreatedDate.isVisible = viewModel.task != null
            if (viewModel.task != null) {
                textCreatedDate.text = viewModel.task!!.createdDateFormated
            }
            checkboxImportant.apply {
                isChecked = viewModel.important.value!!
                setOnCheckedChangeListener { _, b ->
                    viewModel.important.value = b
                }
            }
            fabAddEditTask.setOnClickListener {
                viewModel.onSaveButtonClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEventFlow.collect { event ->
                when (event) {
                    is AddEditTaskEvent.ShowEmptyTaskMessage -> {
                        showEmptyTaskNameMessage(requireView())
                    }
                    is NavigateBackToTaskListingScreen -> {
                        navigateBackToTaskListingScreen(event.result)
                    }
                }
            }
        }
    }

    private fun navigateBackToTaskListingScreen(result: Int) {
        val bundle = bundleOf(ADD_EDIT_TASK_REQUEST to result)
        setFragmentResult(ADD_EDIT_TASK_REQUEST, bundle)
        findNavController().navigateUp()
    }

    private fun showEmptyTaskNameMessage(view: View) {
        Snackbar.make(view, "Task name is Empty", Snackbar.LENGTH_LONG).show()
    }
}