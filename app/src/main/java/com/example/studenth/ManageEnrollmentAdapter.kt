package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.EnrolledStudentData

class ManageEnrollmentAdapter(
    private val studentsList: List<EnrolledStudentData>,
    private val onUnenrollClicked: (EnrolledStudentData) -> Unit
) : RecyclerView.Adapter<ManageEnrollmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enrolled_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = studentsList[position]
        holder.bind(student, onUnenrollClicked)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.student_name_textview)
        private val studentIdTextView: TextView = itemView.findViewById(R.id.student_id_textview)
        private val unenrollButton: Button = itemView.findViewById(R.id.unenroll_button)

        fun bind(student: EnrolledStudentData, onUnenrollClicked: (EnrolledStudentData) -> Unit) {
            studentNameTextView.text = "${student.firstName} ${student.lastName}"
            studentIdTextView.text = student.studentIdNumber ?: "N/A"
            unenrollButton.setOnClickListener { onUnenrollClicked(student) }
        }
    }
}
