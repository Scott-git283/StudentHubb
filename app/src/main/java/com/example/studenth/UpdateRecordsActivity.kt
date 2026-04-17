package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FacultyCourseData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UpdateRecordsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var coursesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_records)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        coursesRecyclerView = findViewById(R.id.courses_recyclerview)
        coursesRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchCoursesData()
    }

    private fun fetchCoursesData() {
        val facultyUid = auth.currentUser?.uid
        if (facultyUid == null) {
            Toast.makeText(this, "Faculty user not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val coursesQuery = db.collection("courses").whereEqualTo("instructor_uid", facultyUid).get().await()
                val coursesList = coursesQuery.documents.mapNotNull { doc ->
                    // Assuming FacultyCourseData has been updated to use String for courseId
                    doc.toObject<FacultyCourseData>()?.apply { courseId = doc.id }
                }

                withContext(Dispatchers.Main) {
                    if (coursesList.isNotEmpty()) {
                        val courseAdapter = UpdateRecordsCourseAdapter(coursesList) { course ->
                            val intent = Intent(this@UpdateRecordsActivity, SelectStudentActivity::class.java).apply {
                                putExtra("COURSE_ID", course.courseId)
                                putExtra("COURSE_NAME", course.courseName)
                            }
                            startActivity(intent)
                        }
                        coursesRecyclerView.adapter = courseAdapter
                    } else {
                        Toast.makeText(this@UpdateRecordsActivity, "You are not assigned to any courses.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("UpdateRecordsActivity", "Error fetching courses", e)
                    Toast.makeText(this@UpdateRecordsActivity, "Failed to load courses: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
