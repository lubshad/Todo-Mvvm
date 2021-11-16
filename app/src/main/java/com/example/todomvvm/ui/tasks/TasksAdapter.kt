package com.example.todomvvm.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.TaskItemBinding

class TasksAdapter(val onItemClickListener: OnItemClickListener) :
    ListAdapter<Task, TasksAdapter.TaskViewHolder>(DiffCallback()) {


    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                checkboxCompleted.setOnCheckedChangeListener { _, value ->
                    val position = adapterPosition
                    val task = getItem(position)
                    onItemClickListener.onCheckboxClick(task, value)
                }
                root.setOnClickListener {
                    val position = adapterPosition
                    val task = getItem(position)
                    onItemClickListener.onItemClick(task)
                }
            }
        }

        fun bind(currentItem: Task) {
            binding.apply {
                textTaskName.text = currentItem.taskName
                checkboxCompleted.isChecked = currentItem.completed
                important.isVisible = currentItem.important
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckboxClick(task: Task, value: Boolean)
    }

}