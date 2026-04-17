package com.example.studenth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyStudentData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewStudentsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var studentsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_students)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        studentsRecyclerView = findViewById(R.id.students_recyclerview)
        studentsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchEnrolledStudents()
    }

    private fun fetchEnrolledStudents() {
        val facultyUser = auth.currentUser
        if (facultyUser == null) {
            Toast.makeText(this, "Faculty user not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Use coroutines to handle the nested queries cleanly
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. Find all student UIDs taught by this faculty
                val enrollmentQuery = db.collection("enrollments")
                    .whereEqualTo("faculty_uid", facultyUser.uid)
                    .get()
                    .await()

                if (enrollmentQuery.isEmpty) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewStudentsActivity, "No students are enrolled in your courses.", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                // Get a distinct list of student UIDs
                val studentUids = enrollmentQuery.documents.mapNotNull { it.getString("student_uid") }.distinct()

                // 2. Fetch the profile for each student UID
                val studentsList = mutableListOf<FacultyStudentData>()
                for (uid in studentUids) {
                    val studentDoc = db.collection("users").document(uid).get().await()
                    if (studentDoc.exists()) {
                        val studentData = studentDoc.toObject<FacultyStudentData>()
                        if (studentData != null) {
                            // Manually copy the fields because toObject() might not map them correctly with underscores
                            studentsList.add(studentData.copy(
                                uid = uid,
                                firstName = studentDoc.getString("first_name"),
                                lastName = studentDoc.getString("last_name"),
                                studentIdNumber = studentDoc.getString("student_id_number")
                            ))
                        }
                    }
                }

                // 3. Display the results on the main thread
                withContext(Dispatchers.Main) {
                    if (studentsList.isNotEmpty()) {
                        val adapter = ViewStudentsAdapter(studentsList)
                        studentsRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@ViewStudentsActivity, "Could not find profile data for enrolled students.", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewStudentsActivity, "Failed to fetch students: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
