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
import com.example.studenth.model.AdminCourseData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ManageCoursesActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var coursesRecyclerView: RecyclerView

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        fetchCourses()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_courses)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        coursesRecyclerView = findViewById(R.id.courses_recyclerview)
        coursesRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.add_course_fab).setOnClickListener {
            val intent = Intent(this, AddCourseActivity::class.java)
            activityLauncher.launch(intent)
        }

        fetchCourses()
    }

    private fun fetchCourses() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val coursesQuery = db.collection("courses").get().await()
                val courseList = mutableListOf<AdminCourseData>()

                for (courseDoc in coursesQuery.documents) {
                    val departmentId = courseDoc.getString("department_id")
                    val instructorUid = courseDoc.getString("instructor_uid")

                    val departmentName = departmentId?.let { db.collection("departments").document(it).get().await().getString("name") } ?: "N/A"
                    val instructorName = instructorUid?.let {
                        val userDoc = db.collection("users").document(it).get().await()
                        "${userDoc.getString("first_name")} ${userDoc.getString("last_name")}"
                    } ?: "N/A"

                    courseList.add(AdminCourseData(
                        id = courseDoc.id,
                        courseName = courseDoc.getString("course_name"),
                        courseCode = courseDoc.getString("course_code"),
                        departmentName = departmentName,
                        instructorName = instructorName
                    ))
                }

                withContext(Dispatchers.Main) {
                    val adapter = ManageCoursesAdapter(courseList, { c -> editCourse(c) }, { c -> showDeleteConfirmation(c) }, { c -> manageEnrollment(c) })
                    coursesRecyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ManageCourses", "Error fetching courses", e)
                    Toast.makeText(this@ManageCoursesActivity, "Failed to fetch courses: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun editCourse(course: AdminCourseData) {
        val intent = Intent(this, EditCourseActivity::class.java).apply {
            putExtra("COURSE_ID", course.id)
        }
        activityLauncher.launch(intent)
    }

    private fun manageEnrollment(course: AdminCourseData) {
        val intent = Intent(this, ManageEnrollmentActivity::class.java).apply {
            putExtra("COURSE_ID", course.id)
            putExtra("COURSE_NAME", course.courseName)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(course: AdminCourseData) {
        AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete '${course.courseName}'?")
            .setPositiveButton("Delete") { _, _ -> deleteCourse(course.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCourse(courseId: String) {
        db.collection("courses").document(courseId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Course deleted successfully.", Toast.LENGTH_SHORT).show()
                fetchCourses()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete course: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
