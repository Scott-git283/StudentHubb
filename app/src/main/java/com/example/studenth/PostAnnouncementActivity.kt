package com.example.studenth

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studenth.model.FacultyCourseData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostAnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var courseSpinner: Spinner
    private var coursesList: List<FacultyCourseData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_announcement)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        courseSpinner = findViewById(R.id.course_spinner)
        val titleEditText = findViewById<TextInputEditText>(R.id.announcement_title_edittext)
        val messageEditText = findViewById<TextInputEditText>(R.id.announcement_message_edittext)
        val postButton = findViewById<Button>(R.id.post_button)

        fetchFacultyCourses()

        postButton.setOnClickListener {
            val selectedCoursePosition = courseSpinner.selectedItemPosition
            if (coursesList.isEmpty() || selectedCoursePosition < 0) {
                Toast.makeText(this, "Please select a course.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val selectedCourseId = coursesList[selectedCoursePosition].courseId

            val title = titleEditText.text.toString().trim()
            val message = messageEditText.text.toString().trim()

            if (title.isNotEmpty() && message.isNotEmpty()) {
                postAnnouncement(selectedCourseId, title, message)
            } else {
                Toast.makeText(this, "Title and message cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchFacultyCourses() {
        val facultyUid = auth.currentUser?.uid
        if (facultyUid == null) {
            Toast.makeText(this, "Faculty user not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fetch courses taught by the faculty
                val coursesQuery = db.collection("courses").whereEqualTo("instructor_uid", facultyUid).get().await()
                coursesList = coursesQuery.documents.mapNotNull { doc ->
                    doc.toObject<FacultyCourseData>()?.apply { courseId = doc.id }
                }

                withContext(Dispatchers.Main) {
                    val courseNames = coursesList.map { "${it.courseCode} - ${it.courseName}" }
                    val adapter = ArrayAdapter(this@PostAnnouncementActivity, android.R.layout.simple_spinner_item, courseNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    courseSpinner.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("PostAnnouncement", "Error fetching courses", e)
                    Toast.makeText(this@PostAnnouncementActivity, "Failed to load courses: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun postAnnouncement(courseId: String, title: String, message: String) {
        val facultyUid = auth.currentUser?.uid
        if (facultyUid == null) {
            Toast.makeText(this, "Cannot post announcement, user not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        val announcement = hashMapOf(
            "title" to title,
            "message" to message,
            "course_id" to courseId,
            "faculty_uid" to facultyUid,
            "created_at" to com.google.firebase.Timestamp.now()
        )

        db.collection("announcements").add(announcement)
            .addOnSuccessListener {
                Toast.makeText(this, "Announcement posted successfully!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to post announcement: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
