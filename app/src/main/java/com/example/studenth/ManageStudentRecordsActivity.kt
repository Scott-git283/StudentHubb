package com.example.studenth

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManageStudentRecordsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var studentUid: String = ""
    private var courseId: String = ""
    private var courseName: String? = ""
    private lateinit var datePickerTextView: TextView
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_student_records)

        db = FirebaseFirestore.getInstance()
        studentUid = intent.getStringExtra("STUDENT_UID") ?: ""
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        courseName = intent.getStringExtra("COURSE_NAME")
        val studentName = intent.getStringExtra("STUDENT_NAME")

        if (studentUid.isEmpty() || courseId.isEmpty()) {
            Toast.makeText(this, "Error: Student or Course ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage $studentName"

        setupAttendanceCard()
        setupResultCard()
    }

    private fun setupAttendanceCard() {
        datePickerTextView = findViewById(R.id.date_picker_textview)
        val attendanceStatusSpinner = findViewById<Spinner>(R.id.attendance_status_spinner)
        val saveAttendanceButton = findViewById<Button>(R.id.save_attendance_button)

        datePickerTextView.setOnClickListener { showDatePickerDialog() }

        val statuses = arrayOf("Present", "Absent", "Late")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        attendanceStatusSpinner.adapter = statusAdapter

        saveAttendanceButton.setOnClickListener {
            val selectedStatus = attendanceStatusSpinner.selectedItem.toString()
            // Check if a date has been picked
            if (datePickerTextView.text != "Select Date") {
                saveAttendance(calendar.time, selectedStatus)
            } else {
                Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupResultCard() {
        val assessmentNameEditText = findViewById<TextInputEditText>(R.id.assessment_name_edittext)
        val marksObtainedEditText = findViewById<TextInputEditText>(R.id.marks_obtained_edittext)
        val totalMarksEditText = findViewById<TextInputEditText>(R.id.total_marks_edittext)
        val gradeEditText = findViewById<TextInputEditText>(R.id.grade_edittext)
        val saveResultButton = findViewById<Button>(R.id.save_result_button)

        saveResultButton.setOnClickListener {
            val assessmentName = assessmentNameEditText.text.toString().trim()
            val marksObtained = marksObtainedEditText.text.toString().toDoubleOrNull()
            val totalMarks = totalMarksEditText.text.toString().toDoubleOrNull()
            val grade = gradeEditText.text.toString().trim()

            if (assessmentName.isNotEmpty() && marksObtained != null && totalMarks != null && grade.isNotEmpty()) {
                saveResult(assessmentName, marksObtained, totalMarks, grade)
            } else {
                Toast.makeText(this, "Please fill all result fields correctly.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateInView() {
        val myFormat = "MMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        datePickerTextView.text = sdf.format(calendar.time)
    }

    private fun saveAttendance(date: java.util.Date, status: String) {
        val attendanceRecord = hashMapOf(
            "student_uid" to studentUid,
            "course_id" to courseId,
            "attendance_date" to Timestamp(date),
            "status" to status,
            "course_name" to courseName
        )

        db.collection("attendance").add(attendanceRecord)
            .addOnSuccessListener {
                Toast.makeText(this, "Attendance saved successfully!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save attendance: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveResult(assessmentName: String, marksObtained: Double, totalMarks: Double, grade: String) {
        val resultRecord = hashMapOf(
            "student_uid" to studentUid,
            "course_id" to courseId,
            "course_name" to courseName,
            "assessment_name" to assessmentName,
            "marks_obtained" to marksObtained,
            "total_marks" to totalMarks,
            "grade" to grade
        )

        db.collection("results").add(resultRecord)
            .addOnSuccessListener {
                Toast.makeText(this, "Result saved successfully!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save result: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
