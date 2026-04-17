package com.example.studenth

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studenth.model.DepartmentData
import com.example.studenth.model.FacultySpinnerData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditCourseActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var courseId: String = ""
    private lateinit var departmentSpinner: Spinner
    private lateinit var instructorSpinner: Spinner
    private var departmentsList: List<DepartmentData> = emptyList()
    private var facultyList: List<FacultySpinnerData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Re-use the layout from AddCourseActivity
        setContentView(R.layout.activity_add_course)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Course" // Set title for editing

        db = FirebaseFirestore.getInstance()
        courseId = intent.getStringExtra("COURSE_ID") ?: ""

        if (courseId.isEmpty()) {
            Toast.makeText(this, "Error: Course ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        departmentSpinner = findViewById(R.id.department_spinner)
        instructorSpinner = findViewById(R.id.instructor_spinner)
        val saveButton = findViewById<Button>(R.id.save_course_button)
        saveButton.text = "Save Changes" // Change button text for editing

        fetchInitialDataAndPopulate()

        saveButton.setOnClickListener {
            saveCourse()
        }
    }

    private fun fetchInitialDataAndPopulate() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fetch all departments and faculty concurrently
                val departmentsQuery = db.collection("departments").get().await()
                val facultyQuery = db.collection("users").whereEqualTo("role", "Faculty").get().await()
                val courseDoc = db.collection("courses").document(courseId).get().await()

                departmentsList = departmentsQuery.documents.mapNotNull { DepartmentData(it.id, it.getString("name"), it.getString("description")) }
                facultyList = facultyQuery.documents.mapNotNull { doc ->
                    val fullName = "${doc.getString("first_name")} ${doc.getString("last_name")}"
                    FacultySpinnerData(doc.id, fullName)
                }

                withContext(Dispatchers.Main) {
                    // Populate spinners
                    val departmentNames = departmentsList.map { it.name }
                    departmentSpinner.adapter = ArrayAdapter(this@EditCourseActivity, android.R.layout.simple_spinner_item, departmentNames)
                    
                    val facultyNames = facultyList.map { it.fullName }
                    instructorSpinner.adapter = ArrayAdapter(this@EditCourseActivity, android.R.layout.simple_spinner_item, facultyNames)

                    // Set initial form values from the course document
                    if (courseDoc.exists()) {
                        findViewById<TextInputEditText>(R.id.course_name_edittext).setText(courseDoc.getString("course_name"))
                        findViewById<TextInputEditText>(R.id.course_code_edittext).setText(courseDoc.getString("course_code"))
                        
                        val deptId = courseDoc.getString("department_id")
                        departmentSpinner.setSelection(departmentsList.indexOfFirst { it.id == deptId })

                        val instId = courseDoc.getString("instructor_uid")
                        instructorSpinner.setSelection(facultyList.indexOfFirst { it.uid == instId })
                    } else {
                        Toast.makeText(this@EditCourseActivity, "Course details not found.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditCourseActivity, "Failed to load initial data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveCourse() {
        val courseName = findViewById<TextInputEditText>(R.id.course_name_edittext).text.toString().trim()
        val courseCode = findViewById<TextInputEditText>(R.id.course_code_edittext).text.toString().trim()

        val selectedDeptPos = departmentSpinner.selectedItemPosition
        val selectedInstPos = instructorSpinner.selectedItemPosition

        if (courseName.isEmpty() || courseCode.isEmpty() || selectedDeptPos < 0 || selectedInstPos < 0) {
            Toast.makeText(this, "Please fill all fields and make a selection.", Toast.LENGTH_SHORT).show()
            return
        }

        val departmentId = departmentsList[selectedDeptPos].id
        val instructorUid = facultyList[selectedInstPos].uid

        val updatedCourse = mapOf(
            "course_name" to courseName,
            "course_code" to courseCode,
            "department_id" to departmentId,
            "instructor_uid" to instructorUid
        )

        db.collection("courses").document(courseId).update(updatedCourse)
            .addOnSuccessListener {
                Toast.makeText(this, "Course updated successfully!", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update course: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
