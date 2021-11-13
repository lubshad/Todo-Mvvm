package com.example.todomvvm.ui.add_edit_task

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.todomvvm.R
import com.example.todomvvm.databinding.FragmentAddEditTaskBinding
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
            editTextTaskName.setText(viewModel.taskName)
            checkboxImportant.isChecked = viewModel.important
            checkboxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = viewModel.task?.createdDateFormated
            fabSaveTask.setOnClickListener {
                viewModel.onSaveButtonClick()
                findNavController().navigateUp()
            }

            editTextTaskName.addTextChangedListener {text->
                viewModel.taskName = text.toString()
            }

            checkboxImportant.setOnCheckedChangeListener { _, checked ->
                viewModel.important = checked
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.addEditTaskEvent.collect { event ->
                    when (event) {
                        AddEditTaskEvent.ShowEmptyTaskNameMessage -> {
                            showEmptyTaskMessage(requireView())
                        }
                    }
                }
            }
        }

    }

    private fun showEmptyTaskMessage(view: View) {
        Snackbar.make(view, "Task name is empty" , Snackbar.LENGTH_LONG).show()
    }
}