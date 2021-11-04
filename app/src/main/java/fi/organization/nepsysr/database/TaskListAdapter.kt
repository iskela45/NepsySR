package fi.organization.nepsysr.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.R
import fi.organization.nepsysr.alarm.AlarmHandler

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.title, current.topic, current.daysRemain, current.requestCode, current.timer, current.taskId)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskItemView: TextView = itemView.findViewById(R.id.textView)

        var resButton: Button = itemView.findViewById(R.id.resButton)
        var context : Context = resButton.context
        val alarm = AlarmHandler(context)

        fun bind(title: String?, topic: String, daysRemain: Int, requestCode: Int, timer: Int, taskId: Int) {
            taskItemView.text = "$title \n$topic \n$daysRemain/$timer päivää jäljellä"

            resButton.setOnClickListener {
                alarm.updateSpecificTaskAlarm(taskId, timer, requestCode, title!!, topic)
            }
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_task, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        // Can be used to compare details such as name, modify/copy as needed
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }
    }
}
