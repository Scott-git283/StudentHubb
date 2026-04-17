package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyStudentData // Re-using this simple student model

class EnrollStudentAdapter(
    private val students: List<FacultyStudentData>,
    private val onEnrollClicked: (FacultyStudentData) -> Unit
) : RecyclerView.Adapter<EnrollStudentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enroll_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student, onEnrollClicked)
    }

    override fun getItemCount(): Int = students.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.student_name_textview)
        private val idTextView: TextView = itemView.findViewById(R.id.student_id_textview)
        private val enrollButton: Button = itemView.findViewById(R.id.enroll_button)

        fun bind(student: FacultyStudentData, onEnroll: (FacultyStudentData) -> Unit) {
            nameTextView.text = "${student.firstName} ${student.lastName}"
            idTextView.text = student.studentIdNumber ?: "N/A"
            enrollButton.setOnClickListener { onEnroll(student) }
        }
    }
}
