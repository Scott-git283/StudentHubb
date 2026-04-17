package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyStudentData

class ViewStudentsAdapter(private val studentsList: List<FacultyStudentData>) : RecyclerView.Adapter<ViewStudentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val studentData = studentsList[position]
        holder.bind(studentData)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.student_name_textview)
        private val studentIdTextView: TextView = itemView.findViewById(R.id.student_id_textview)

        fun bind(studentData: FacultyStudentData) {
            studentNameTextView.text = "${studentData.firstName} ${studentData.lastName}"
            studentIdTextView.text = studentData.studentIdNumber ?: "N/A"
        }
    }
}
