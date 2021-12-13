package fi.organization.nepsysr.database

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.AddingTaskActivity
import fi.organization.nepsysr.R
import fi.organization.nepsysr.alarm.AlarmHandler
import fi.organization.nepsysr.utilities.convertBitmap

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
        private val taskItemViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val taskItemViewTopic: TextView = itemView.findViewById(R.id.textViewTopic)
        private var taskImageView: ImageView = itemView.findViewById(R.id.profile_Img)
        private val taskTextView: LinearLayout = itemView.findViewById(R.id.taskTextView)

        // initialization for resButton and it's context
        private var resButton: Button = itemView.findViewById(R.id.resButton)
        var context : Context = resButton.context
        val alarm = AlarmHandler(context)
        private var mContext : Context = itemView.context

        // initialization for list item and it's context

        fun bind(title: String?,
                 topic: String,
                 daysRemain: Int,
                 requestCode: Int,
                 timer: Int, taskId:
                 Int, contactUserId:
                 Int, img: Bitmap
        ) {
            taskItemViewTitle.text = "$title"
            taskItemViewTopic.text = "$topic \n$daysRemain/$timer päivää jäljellä"
            taskImageView.setImageBitmap(img)

            val addImageDialog = AlertDialog.Builder(mContext)
                .setTitle("Lisää kuva")
                .setMessage("Lisätäänkö kuva kamerasta vai galleriasta?")
                .setPositiveButton("Kamera") { _, _ ->
                    openCamera(taskId)
                }
                .setNegativeButton("Galleria") { _, _ ->
                    openGallery(taskId)
                }

            // Check and ask for permissions, then start camera activity.
            taskImageView.setOnClickListener {
                addImageDialog.show()
            }

            resButton.setOnClickListener {
                alarm.updateSpecificTaskAlarm(taskId, timer, requestCode, title!!, topic)
            }

            // Add clickListener to open addTaskActivity with prefilled
            // saving will update task
            taskTextView.setOnClickListener{
                val intent = Intent(mContext, AddingTaskActivity::class.java)
                intent.putExtra("contactUserId", contactUserId)
                intent.putExtra("taskId", taskId)
                intent.putExtra("title", title)
                intent.putExtra("topic", topic)
                intent.putExtra("daysRemain", daysRemain )
                intent.putExtra("timer", timer )
                intent.putExtra("img", convertBitmap(img))
                intent.putExtra("isUpdate", true)

                ActivityCompat.startActivityForResult(
                    mContext as Activity,
                    intent,
                    2000,
                    null
                )
            }
        }

        companion object {
            private const val IMAGE_PICK_CODE = 1

            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item_task, parent, false)
                return TaskViewHolder(view)
            }
        }

        private fun openCamera(taskId: Int) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.CAMERA
                ) -> {
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

        private fun openGallery(taskId: Int) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    // You can use the API that requires the permission.
                    val gallery = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    )

                    ActivityCompat.startActivityForResult(
                        mContext as Activity,
                        gallery,
                        taskId,
                        null
                    )
                }
                else -> {
                    // You can directly ask for the permission.
                    ActivityCompat.requestPermissions(
                        mContext as Activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1001
                    )
                }
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
