package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.EnrolledStudentData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ManageEnrollmentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var courseId: String = ""
    private lateinit var enrolledStudentsRecyclerView: RecyclerView

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchEnrolledStudents()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_enrollment)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        courseId = intent.getStringExtra("COURSE_ID") ?: ""
        val courseName = intent.getStringExtra("COURSE_NAME")
        supportActionBar?.title = "Enrollments: $courseName"

        if (courseId.isEmpty()) {
            Toast.makeText(this, "Error: Course ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        enrolledStudentsRecyclerView = findViewById(R.id.enrolled_students_recyclerview)
        enrolledStudentsRecyclerView.layoutManager = LinearLayoutManager(this)

        val addEnrollmentFab = findViewById<FloatingActionButton>(R.id.add_enrollment_fab)
        addEnrollmentFab.setOnClickListener {
            val intent = Intent(this, EnrollStudentActivity::class.java).apply {
                putExtra("COURSE_ID", courseId)
            }
            activityLauncher.launch(intent)
        }

        fetchEnrolledStudents()
    }

    private fun fetchEnrolledStudents() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val enrollmentQuery = db.collection("enrollments").whereEqualTo("course_id", courseId).get().await()
                val studentList = mutableListOf<EnrolledStudentData>()

                for (enrollmentDoc in enrollmentQuery.documents) {
                    val studentUid = enrollmentDoc.getString("student_uid")
                    if (studentUid != null) {
                        val userDoc = db.collection("users").document(studentUid).get().await()
                        if (userDoc.exists()) {
                            studentList.add(EnrolledStudentData(
                                enrollmentId = enrollmentDoc.id,
                                studentUid = studentUid,
                                firstName = userDoc.getString("first_name"),
                                lastName = userDoc.getString("last_name"),
                                studentIdNumber = userDoc.getString("student_id_number")
                            ))
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    if (studentList.isNotEmpty()) {
                        val adapter = ManageEnrollmentAdapter(studentList) { student ->
                            showUnenrollConfirmationDialog(student)
                        }
                        enrolledStudentsRecyclerView.adapter = adapter
                    } else {
                        enrolledStudentsRecyclerView.adapter = null
                        Toast.makeText(this@ManageEnrollmentActivity, "No students are enrolled in this course.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ManageEnrollment", "Error fetching students", e)
                    Toast.makeText(this@ManageEnrollmentActivity, "Failed to fetch students: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showUnenrollConfirmationDialog(student: EnrolledStudentData) {
        AlertDialog.Builder(this)
            .setTitle("Unenroll Student")
            .setMessage("Are you sure you want to unenroll ${student.firstName} ${student.lastName} from this course?")
            .setPositiveButton("Unenroll") { _, _ -> unenrollStudent(student.enrollmentId) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun unenrollStudent(enrollmentId: String) {
        db.collection("enrollments").document(enrollmentId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Student unenrolled successfully!", Toast.LENGTH_SHORT).show()
                fetchEnrolledStudents() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to unenroll student: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
