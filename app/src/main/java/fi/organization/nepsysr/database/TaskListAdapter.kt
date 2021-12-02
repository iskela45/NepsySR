package fi.organization.nepsysr.database

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.AddingTaskActivity
import fi.organization.nepsysr.R
import fi.organization.nepsysr.alarm.AlarmHandler

class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(
            current.title,
            current.topic,
            current.daysRemain,
            current.requestCode,
            current.timer,
            current.taskId,
            current.contactId,
            current.img
        )
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskItemView: TextView = itemView.findViewById(R.id.textView)
        var taskImageView: ImageView = itemView.findViewById(R.id.profile_Img)

        // initialization for resButton and it's context
        var resButton: Button = itemView.findViewById(R.id.resButton)
        var context : Context = resButton.context
        val alarm = AlarmHandler(context)
        var mContext : Context = itemView.context

        // initialization for list item and it's context

        fun bind(title: String?,
                 topic: String,
                 daysRemain: Int,
                 requestCode: Int,
                 timer: Int, taskId:
                 Int, contactUserId:
                 Int, img: Bitmap
        ) {
            taskItemView.text = "$title \n$topic \n$daysRemain/$timer päivää jäljellä"
            taskImageView.setImageBitmap(img)

            resButton.setOnClickListener {
                alarm.updateSpecificTaskAlarm(taskId, timer, requestCode, title!!, topic)
            }

            // Check and ask for permissions, then start camera activity.
            taskImageView.setOnClickListener {
                when {
                    ContextCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // You can use the API that requires the permission.
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        ActivityCompat.startActivityForResult(
                            mContext as Activity,
                            takePictureIntent,
                            taskId,
                            null
                        )
                    }

                    else -> {
                        // You can directly ask for the permission.
                        ActivityCompat.requestPermissions(
                            mContext as Activity,
                            arrayOf(Manifest.permission.CAMERA),
                            IMAGE_PICK_CODE)
                    }
                }
            }
            // Add clicklistener to open addTaskActivity with prefilled
            // saving will update task
            taskItemView.setOnClickListener{
                val intent = Intent(mContext, AddingTaskActivity::class.java)
                intent.putExtra("contactUserId", contactUserId)
                intent.putExtra("taskId", taskId)
                intent.putExtra("title", title)
                intent.putExtra("topic", topic)
                intent.putExtra("daysRemain", daysRemain )
                intent.putExtra("timer", timer )
                intent.putExtra("img", img)
                intent.putExtra("update", true)

                ActivityCompat.startActivityForResult(
                    mContext as Activity,
                    intent,
                    2000,
                    null
                )
            }
        }

        companion object {
            private val IMAGE_PICK_CODE = 1

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
