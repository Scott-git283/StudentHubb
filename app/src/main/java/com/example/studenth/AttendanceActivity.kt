package com.example.studenth

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FirebaseAttendanceData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var attendanceRecyclerView: RecyclerView
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        attendanceRecyclerView = findViewById(R.id.attendance_recyclerview)
        attendanceRecyclerView.layoutManager = LinearLayoutManager(this)
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)

        setupSpinners()
    }

    private fun setupSpinners() {
        val months = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear - 5..currentYear).map { it.toString() }.toTypedArray()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // Set default selection to current month and year
        val calendar = Calendar.getInstance()
        monthSpinner.setSelection(calendar.get(Calendar.MONTH))
        yearSpinner.setSelection(years.indexOf(currentYear.toString()))

        val selectionListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchAttendance()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        monthSpinner.onItemSelectedListener = selectionListener
        yearSpinner.onItemSelectedListener = selectionListener
    }

    private fun fetchAttendance() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedMonth = monthSpinner.selectedItemPosition
        val selectedYear = yearSpinner.selectedItem.toString().toInt()

        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth, 1, 0, 0, 0)
        val startDate = Timestamp(calendar.time)

        calendar.add(Calendar.MONTH, 1)
        val endDate = Timestamp(calendar.time)

        db.collection("attendance")
            .whereEqualTo("student_uid", user.uid)
            .whereGreaterThanOrEqualTo("attendance_date", startDate)
            .whereLessThan("attendance_date", endDate)
            .orderBy("attendance_date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    attendanceRecyclerView.adapter = null
                    Toast.makeText(this, "No attendance records found for this month.", Toast.LENGTH_SHORT).show()
                } else {
                    val attendanceList = documents.toObjects(FirebaseAttendanceData::class.java)
                    val adapter = AttendanceAdapter(attendanceList)
                    attendanceRecyclerView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch attendance: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
