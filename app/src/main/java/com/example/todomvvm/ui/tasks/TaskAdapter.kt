
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.TaskItemBinding

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context))
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class  TaskViewHolder(private val binding: TaskItemBinding)    :RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                checkboxCompleted.isChecked = task.completed
                textViewTaskName.text = task.name
                textViewTaskName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important

            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

}