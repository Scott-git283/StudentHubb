package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.AdminCourseData
import com.google.android.material.button.MaterialButton

class ManageCoursesAdapter(
    private val courses: List<AdminCourseData>,
    private val onEdit: (AdminCourseData) -> Unit,
    private val onDelete: (AdminCourseData) -> Unit,
    private val onManageEnrollment: (AdminCourseData) -> Unit
) : RecyclerView.Adapter<ManageCoursesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_management, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(courses[position], onEdit, onDelete, onManageEnrollment)
    }

    override fun getItemCount() = courses.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_textview)
        private val courseCodeTextView: TextView = itemView.findViewById(R.id.course_code_textview)
        private val departmentTextView: TextView = itemView.findViewById(R.id.department_textview)
        private val instructorTextView: TextView = itemView.findViewById(R.id.instructor_textview)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        private val manageEnrollmentButton: MaterialButton = itemView.findViewById(R.id.manage_enrollment_button)

        fun bind(course: AdminCourseData, onEdit: (AdminCourseData) -> Unit, onDelete: (AdminCourseData) -> Unit, onManageEnrollment: (AdminCourseData) -> Unit) {
            courseNameTextView.text = course.courseName
            courseCodeTextView.text = course.courseCode
            departmentTextView.text = "Dept: ${course.departmentName ?: "N/A"}"
            instructorTextView.text = "Instructor: ${course.instructorName ?: "N/A"}"
            editButton.setOnClickListener { onEdit(course) }
            deleteButton.setOnClickListener { onDelete(course) }
            manageEnrollmentButton.setOnClickListener { onManageEnrollment(course) }
        }
    }
}
