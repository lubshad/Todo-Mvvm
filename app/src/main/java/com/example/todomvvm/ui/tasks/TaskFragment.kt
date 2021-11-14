package com.example.todomvvm.ui.tasks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.todomvvm.R
import com.example.todomvvm.databinding.FragmentTasksBinding

class TaskFragment : Fragment(R.layout.fragment_tasks) {


    private lateinit var binding: FragmentTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentTasksBinding.inflate(layoutInflater)
        Log.i("TaskFragment", "fab click")
        binding.apply {
            fabAddTask.setOnClickListener {

            }
        }
    }

}