package com.example.studenth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.StudentAnnouncementData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewAnnouncementsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var announcementsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_announcements)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        announcementsRecyclerView = findViewById(R.id.announcements_recyclerview)
        announcementsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        val studentUid = auth.currentUser?.uid
        if (studentUid == null) {
            Toast.makeText(this, "Student not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // 1. Get the list of courses the student is enrolled in
                val enrollmentsQuery = db.collection("enrollments").whereEqualTo("student_uid", studentUid).get().await()
                if (enrollmentsQuery.isEmpty) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewAnnouncementsActivity, "You are not enrolled in any courses.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val enrolledCourseIds = enrollmentsQuery.documents.mapNotNull { it.getString("course_id") }

                // 2. Fetch announcements for those courses
                if (enrolledCourseIds.isEmpty()) return@launch
                val announcementsQuery = db.collection("announcements")
                    .whereIn("course_id", enrolledCourseIds)
                    .orderBy("created_at", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val announcementList = mutableListOf<StudentAnnouncementData>()
                for (announcementDoc in announcementsQuery.documents) {
                    val courseId = announcementDoc.getString("course_id") ?: ""
                    val facultyUid = announcementDoc.getString("faculty_uid") ?: ""

                    // 3. Fetch additional details (course name, faculty name)
                    val courseDoc = db.collection("courses").document(courseId).get().await()
                    val facultyDoc = db.collection("users").document(facultyUid).get().await()

                    val courseName = courseDoc.getString("course_name")
                    val facultyName = "${facultyDoc.getString("first_name")} ${facultyDoc.getString("last_name")}"
                    
                    announcementList.add(StudentAnnouncementData(
                        title = announcementDoc.getString("title"),
                        message = announcementDoc.getString("message"),
                        createdAt = announcementDoc.getTimestamp("created_at"),
                        courseName = courseName,
                        facultyName = facultyName
                    ))
                }

                withContext(Dispatchers.Main) {
                    if (announcementList.isNotEmpty()) {
                        val adapter = AnnouncementAdapter(announcementList)
                        announcementsRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@ViewAnnouncementsActivity, "No announcements found for your courses.", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ViewAnnouncements", "Error fetching announcements", e)
                    Toast.makeText(this@ViewAnnouncementsActivity, "Failed to load announcements: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
