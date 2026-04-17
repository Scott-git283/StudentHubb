package com.example.studenth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyStudentData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EnrollStudentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var courseId: String = ""
    private lateinit var unenrolledStudentsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enroll_student)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Enroll New Student"

        db = FirebaseFirestore.getInstance()
        courseId = intent.getStringExtra("COURSE_ID") ?: ""

        if (courseId.isEmpty()) {
            Toast.makeText(this, "Error: Course ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        unenrolledStudentsRecyclerView = findViewById(R.id.unenrolled_students_recyclerview)
        unenrolledStudentsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchUnenrolledStudents()
    }

    private fun fetchUnenrolledStudents() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. Get all users who are students
                val allStudentsQuery = db.collection("users").whereEqualTo("role", "Student").get().await()
                val allStudents = allStudentsQuery.documents.mapNotNull { doc ->
                    FacultyStudentData(
                        uid = doc.id,
                        firstName = doc.getString("first_name"),
                        lastName = doc.getString("last_name"),
                        studentIdNumber = doc.getString("student_id_number")
                    )
                }

                // 2. Get all students already enrolled in this course
                val enrollmentsQuery = db.collection("enrollments").whereEqualTo("course_id", courseId).get().await()
                val enrolledStudentUids = enrollmentsQuery.documents.mapNotNull { it.getString("student_uid") }.toSet()

                // 3. Find the difference
                val unenrolledStudents = allStudents.filter { it.uid !in enrolledStudentUids }

                withContext(Dispatchers.Main) {
                    if (unenrolledStudents.isNotEmpty()) {
                        val adapter = EnrollStudentAdapter(unenrolledStudents) { student ->
                            enrollStudent(student)
                        }
                        unenrolledStudentsRecyclerView.adapter = adapter
                    } else {
                        unenrolledStudentsRecyclerView.adapter = null
                        Toast.makeText(this@EnrollStudentActivity, "All students are already enrolled in this course.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("EnrollStudentActivity", "Error fetching unenrolled students", e)
                    Toast.makeText(this@EnrollStudentActivity, "Failed to fetch student list: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enrollStudent(student: FacultyStudentData) {
        // We need to find the faculty UID for the current course
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val courseDoc = db.collection("courses").document(courseId).get().await()
                val facultyUid = courseDoc.getString("instructor_uid")

                if (facultyUid == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EnrollStudentActivity, "Could not find instructor for this course.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val enrollmentData = hashMapOf(
                    "student_uid" to student.uid,
                    "course_id" to courseId,
                    "faculty_uid" to facultyUid
                )

                db.collection("enrollments").add(enrollmentData).await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EnrollStudentActivity, "${student.firstName} enrolled successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    // Re-fetch to update the list, removing the newly enrolled student
                    fetchUnenrolledStudents()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EnrollStudentActivity, "Enrollment failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
