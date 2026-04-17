package com.example.studenth

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FirebaseAttendanceData
import java.text.SimpleDateFormat
import java.util.*

class AttendanceAdapter(private val attendanceList: List<FirebaseAttendanceData>) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attendanceData = attendanceList[position]
        holder.bind(attendanceData)
    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTextView: TextView = itemView.findViewById(R.id.course_name_textview)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_textview)
        private val statusTextView: TextView = itemView.findViewById(R.id.status_textview)

        fun bind(attendanceData: FirebaseAttendanceData) {
            courseNameTextView.text = attendanceData.courseName
            
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            attendanceData.attendanceDate?.toDate()?.let {
                dateTextView.text = sdf.format(it)
            }
            
            statusTextView.text = attendanceData.status

            when (attendanceData.status) {
                "Present" -> statusTextView.setTextColor(Color.parseColor("#388E3C")) // Green
                "Absent" -> statusTextView.setTextColor(Color.parseColor("#D32F2F")) // Red
                else -> statusTextView.setTextColor(Color.BLACK)
            }
        }
    }
}
