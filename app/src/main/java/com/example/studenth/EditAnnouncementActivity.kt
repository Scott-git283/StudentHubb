package com.example.studenth

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studenth.model.AnnouncementDetails
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

class EditAnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var courseSpinner: Spinner
    
    private var announcementId: String = ""
    private var coursesList: List<FacultyCourseData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_announcement) // Reuse layout

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Announcement"

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        announcementId = intent.getStringExtra("ANNOUNCEMENT_ID") ?: ""

        if (announcementId.isEmpty()) {
            Toast.makeText(this, "Error: Announcement ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        courseSpinner = findViewById(R.id.course_spinner)
        val saveButton = findViewById<Button>(R.id.post_button)
        saveButton.text = "Update Announcement"

        fetchInitialDataAndPopulate()

        saveButton.setOnClickListener { saveAnnouncement() }
    }

    private fun fetchInitialDataAndPopulate() {
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

                // Fetch the specific announcement details
                val detailsDoc = db.collection("announcements").document(announcementId).get().await()

                withContext(Dispatchers.Main) {
                    // Populate the course spinner
                    val courseNames = coursesList.map { "${it.courseCode} - ${it.courseName}" }
                    val adapter = ArrayAdapter(this@EditAnnouncementActivity, android.R.layout.simple_spinner_item, courseNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    courseSpinner.adapter = adapter

                    // Populate the form with announcement details
                    if (detailsDoc.exists()) {
                        val announcementDetails = detailsDoc.toObject<AnnouncementDetails>()
                        findViewById<TextInputEditText>(R.id.announcement_title_edittext).setText(announcementDetails?.title)
                        findViewById<TextInputEditText>(R.id.announcement_message_edittext).setText(announcementDetails?.message)
                        
                        val courseId = announcementDetails?.course_id
                        val coursePosition = coursesList.indexOfFirst { it.courseId == courseId }
                        if (coursePosition >= 0) {
                            courseSpinner.setSelection(coursePosition)
                        }
                    } else {
                        Toast.makeText(this@EditAnnouncementActivity, "Failed to load announcement details.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("EditAnnouncement", "Error fetching data", e)
                    Toast.makeText(this@EditAnnouncementActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveAnnouncement() {
        val selectedCoursePosition = courseSpinner.selectedItemPosition
        if (coursesList.isEmpty() || selectedCoursePosition < 0) {
            Toast.makeText(this, "Please select a course.", Toast.LENGTH_SHORT).show()
            return
        }
        val courseId = coursesList[selectedCoursePosition].courseId

        val title = findViewById<TextInputEditText>(R.id.announcement_title_edittext).text.toString().trim()
        val message = findViewById<TextInputEditText>(R.id.announcement_message_edittext).text.toString().trim()

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Title and message cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAnnouncement = mapOf(
            "title" to title,
            "message" to message,
            "course_id" to courseId
        )

        db.collection("announcements").document(announcementId)
            .update(updatedAnnouncement)
            .addOnSuccessListener {
                Toast.makeText(this, "Announcement updated successfully!", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update announcement: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
