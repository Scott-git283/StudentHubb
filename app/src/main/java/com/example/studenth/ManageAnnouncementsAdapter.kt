package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.ManagedAnnouncement
import java.text.SimpleDateFormat
import java.util.*

class ManageAnnouncementsAdapter(
    private val announcementsList: List<ManagedAnnouncement>,
    private val onEditClicked: (ManagedAnnouncement) -> Unit,
    private val onDeleteClicked: (ManagedAnnouncement) -> Unit
) : RecyclerView.Adapter<ManageAnnouncementsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcement = announcementsList[position]
        holder.bind(announcement, onEditClicked, onDeleteClicked)
        holder.itemView.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.item_animation_fall_down)
    }

    override fun getItemCount(): Int {
        return announcementsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.announcement_title_textview)
        private val messageTextView: TextView = itemView.findViewById(R.id.announcement_message_textview)
        private val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_textview)
        private val dateTextView: TextView = itemView.findViewById(R.id.post_date_textview)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(
            announcement: ManagedAnnouncement,
            onEditClicked: (ManagedAnnouncement) -> Unit,
            onDeleteClicked: (ManagedAnnouncement) -> Unit
        ) {
            titleTextView.text = announcement.title
            messageTextView.text = announcement.message
            courseNameTextView.text = announcement.courseName ?: "N/A"

            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            announcement.createdAt?.toDate()?.let {
                dateTextView.text = "Posted on: ${sdf.format(it)}"
            } ?: run {
                dateTextView.text = "Posted on: Unknown"
            }

            editButton.setOnClickListener { onEditClicked(announcement) }
            deleteButton.setOnClickListener { onDeleteClicked(announcement) }
        }
    }
}
