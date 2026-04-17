package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FirebaseResultData

class ResultsAdapter(private val resultsList: List<FirebaseResultData>) : RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resultData = resultsList[position]
        holder.bind(resultData)
    }

    override fun getItemCount(): Int {
        return resultsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_textview)
        private val assessmentNameTextView: TextView = itemView.findViewById(R.id.assessment_name_textview)
        private val marksTextView: TextView = itemView.findViewById(R.id.marks_textview)
        private val gradeTextView: TextView = itemView.findViewById(R.id.grade_textview)

        fun bind(resultData: FirebaseResultData) {
            courseNameTextView.text = resultData.courseName
            assessmentNameTextView.text = resultData.assessmentName
            marksTextView.text = "Marks: ${resultData.marksObtained} / ${resultData.totalMarks}"
            gradeTextView.text = resultData.grade

            // Set grade color based on performance
            val context = itemView.context
            val gradeColor = when (resultData.grade.toUpperCase()) {
                "A+", "A", "A-" -> R.color.grade_good
                "B+", "B", "B-" -> R.color.grade_average
                else -> R.color.grade_bad
            }
            gradeTextView.setBackgroundColor(ContextCompat.getColor(context, gradeColor))
        }
    }
}
