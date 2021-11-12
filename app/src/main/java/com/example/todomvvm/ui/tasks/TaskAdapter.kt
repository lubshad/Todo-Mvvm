import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todomvvm.data.Task
import com.example.todomvvm.databinding.TaskItemBinding

class TaskAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context))
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {

                        val task = getItem(position)
                        onClickListener.onItemClick(task)
                    }
                }
                checkboxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {

                        val task = getItem(position)
                        onClickListener.onCheckboxClick(task)
                    }
                }
            }

        }


        fun bind(task: Task) {
            binding.apply {
                checkboxCompleted.isChecked = task.completed
                textViewTaskName.text = task.name
                textViewTaskName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important

            }
        }
    }

    interface OnClickListener {
        fun onItemClick(task: Task)
        fun onCheckboxClick(task: Task)
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