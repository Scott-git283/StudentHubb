package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyCourseData

class UpdateRecordsCourseAdapter(
    private val coursesList: List<FacultyCourseData>,
    private val onItemClicked: (FacultyCourseData) -> Unit
) : RecyclerView.Adapter<UpdateRecordsCourseAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courseData = coursesList[position]
        holder.bind(courseData, onItemClicked)
    }

    override fun getItemCount(): Int {
        return coursesList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_textview)
        private val courseCodeTextView: TextView = itemView.findViewById(R.id.course_code_textview)

        fun bind(courseData: FacultyCourseData, onItemClicked: (FacultyCourseData) -> Unit) {
            courseNameTextView.text = courseData.courseName
            courseCodeTextView.text = courseData.courseCode
            itemView.setOnClickListener { onItemClicked(courseData) }
        }
    }
}
