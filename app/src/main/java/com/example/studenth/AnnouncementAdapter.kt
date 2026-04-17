package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.StudentAnnouncementData
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementAdapter(private val announcementsList: List<StudentAnnouncementData>) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val announcementData = announcementsList[position]
        holder.bind(announcementData)
    }

    override fun getItemCount(): Int {
        return announcementsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.announcement_title_textview)
        private val messageTextView: TextView = itemView.findViewById(R.id.announcement_message_textview)
        private val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_textview)
        private val facultyNameTextView: TextView = itemView.findViewById(R.id.faculty_name_textview)
        private val dateTextView: TextView = itemView.findViewById(R.id.post_date_textview)

        fun bind(announcement: StudentAnnouncementData) {
            titleTextView.text = announcement.title
            messageTextView.text = announcement.message
            courseNameTextView.text = announcement.courseName ?: "N/A"
            facultyNameTextView.text = "Posted by: ${announcement.facultyName ?: "Unknown"}"

            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            announcement.createdAt?.toDate()?.let {
                dateTextView.text = sdf.format(it)
            } ?: run {
                dateTextView.text = ""
            }
        }
    }
}
