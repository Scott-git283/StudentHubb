package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyStudentData

class StudentSelectionAdapter(
    private val students: List<FacultyStudentData>,
    private val onStudentClicked: (FacultyStudentData) -> Unit
) : RecyclerView.Adapter<StudentSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
        holder.itemView.setOnClickListener { onStudentClicked(student) }
    }

    override fun getItemCount(): Int = students.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.student_name_textview)
        private val idTextView: TextView = itemView.findViewById(R.id.student_id_textview)

        fun bind(student: FacultyStudentData) {
            nameTextView.text = "${student.firstName} ${student.lastName}"
            idTextView.text = student.studentIdNumber ?: "N/A"
        }
    }
}
