package com.example.todomvvm.ui.add_edit_tasks

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todomvvm.R
import com.example.todomvvm.databinding.FragmentAddEditTaskBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


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
            viewModel.addEditTaskEventFlow.collect{ event ->
                when(event) {
                    is AddEditTaskEvent.ShowEmptyTaskMessage-> {
                        showEmptyTaskNameMessage(requireView())
                    }
                    is AddEditTaskEvent.NavigateBackToTaskListingScreen -> {
                          navigateBackToTaskListingScreen()
                    }
                }
            }
        }
    }

    private fun navigateBackToTaskListingScreen() {
        FragmentManager.setFragmentResult()
        findNavController().navigateUp()
    }

    private fun showEmptyTaskNameMessage(view: View) {
        Snackbar.make(view , "Task name is Empty" , Snackbar.LENGTH_LONG).show()
    }
}