package fi.organization.nepsysr.database

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fi.organization.nepsysr.ProfileActivity
import fi.organization.nepsysr.R
import fi.organization.nepsysr.TaskActivity
import fi.organization.nepsysr.utilities.convertBitmap

class ContactListAdapter : ListAdapter<Contact,
                             ContactListAdapter.ContactViewHolder>(ContactsComparator()),
                             ActivityCompat.OnRequestPermissionsResultCallback {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.uid, current.name, current.img, current.color)
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactItemView: TextView = itemView.findViewById(R.id.textView)
        private var contactImageView: ImageView = itemView.findViewById(R.id.profile_Img)

        var context : Context = contactImageView.context
        private var mContext : Context = itemView.context

        fun bind(id: Int, text: String?, img: Bitmap?, color: String?) {
            contactItemView.text = text
            contactItemView.setBackgroundColor(Color.parseColor(color))
            contactImageView.setImageBitmap(img)

            contactItemView.setOnClickListener {
                val intent = Intent(mContext, TaskActivity::class.java)
                intent.putExtra("uid", id)
                startActivityForResult(mContext as Activity, intent, 3000, null)
            }

            contactItemView.setOnLongClickListener {
                val intent = Intent(mContext, ProfileActivity::class.java)
                intent.putExtra("uid", id)
                intent.putExtra("name", text)
                // There is no way to null the image.
                intent.putExtra("img", convertBitmap(img!!))
                intent.putExtra("color", color)
                intent.putExtra("isUpdate", true)
                startActivityForResult(mContext as Activity, intent, 2000, null)
                return@setOnLongClickListener true
            }


            // Check and ask for permissions, then start gallery activity.
            contactImageView.setOnClickListener {
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

                        startActivityForResult(mContext as Activity, gallery, id, null)
                    }
                    else -> {
                        // You can directly ask for the permission.
                        requestPermissions(
                            mContext as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            IMAGE_PICK_CODE)
                    }
                }
            }
        }

        companion object {
            private const val IMAGE_PICK_CODE = 1000

            fun create(parent: ViewGroup): ContactViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recyclerview_item, parent, false)
                return ContactViewHolder(view)
            }
        }
    }

    class ContactsComparator : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }

        // Can be used to compare details such as name, modify/copy as needed
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "camera1")
        }

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "camera2")
        }
    }
}
