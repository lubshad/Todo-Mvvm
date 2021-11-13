package com.example.todomvvm.ui.confirm_dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.todomvvm.ui.tasks.DELETE_ALL_COMPLETED
import com.example.todomvvm.ui.tasks.DELETE_ALL_COMPLETED_RESULT_KEY


class ConfirmDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Delete All Completed")
            .setMessage("Are you sure wanna delete all completed tasks")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                val bundle = bundleOf(DELETE_ALL_COMPLETED_RESULT_KEY to DELETE_ALL_COMPLETED)
                setFragmentResult(DELETE_ALL_COMPLETED_RESULT_KEY, bundle)
            }
            .create()

}