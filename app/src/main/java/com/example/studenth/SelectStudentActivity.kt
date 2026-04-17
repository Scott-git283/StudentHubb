package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyStudentData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SelectStudentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var studentsRecyclerView: RecyclerView
    private var courseId: String = ""
    private var courseName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_student)

        db = FirebaseFirestore.getInstance()
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        courseName = intent.getStringExtra("COURSE_NAME")

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select Student in $courseName"

        if (courseId.isEmpty()) {
            Toast.makeText(this, "Error: Course ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        studentsRecyclerView = findViewById(R.id.students_recyclerview)
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchEnrolledStudents()
    }

    private fun fetchEnrolledStudents() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val enrollmentQuery = db.collection("enrollments").whereEqualTo("course_id", courseId).get().await()
                val studentList = mutableListOf<FacultyStudentData>()

                for (enrollmentDoc in enrollmentQuery.documents) {
                    val studentUid = enrollmentDoc.getString("student_uid")
                    if (studentUid != null) {
                        val userDoc = db.collection("users").document(studentUid).get().await()
                        if (userDoc.exists()) {
                            studentList.add(FacultyStudentData(
                                uid = studentUid,
                                firstName = userDoc.getString("first_name"),
                                lastName = userDoc.getString("last_name"),
                                studentIdNumber = userDoc.getString("student_id_number")
                            ))
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    if (studentList.isNotEmpty()) {
                        val adapter = StudentSelectionAdapter(studentList) { student ->
                            val intent = Intent(this@SelectStudentActivity, ManageStudentRecordsActivity::class.java).apply {
                                putExtra("STUDENT_UID", student.uid)
                                putExtra("STUDENT_NAME", "${student.firstName} ${student.lastName}")
                                putExtra("COURSE_ID", courseId)
                                putExtra("COURSE_NAME", courseName)
                            }
                            startActivity(intent)
                        }
                        studentsRecyclerView.adapter = adapter
                    } else {
                        studentsRecyclerView.adapter = null
                        Toast.makeText(this@SelectStudentActivity, "No students enrolled in this course.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("SelectStudentActivity", "Error fetching students", e)
                    Toast.makeText(this@SelectStudentActivity, "Failed to fetch students: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
