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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddCourseActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var departmentSpinner: Spinner
    private lateinit var instructorSpinner: Spinner
    private var departmentsList: List<DepartmentData> = emptyList()
    private var facultyList: List<FacultySpinnerData> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_course)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        departmentSpinner = findViewById(R.id.department_spinner)
        instructorSpinner = findViewById(R.id.instructor_spinner)
        val saveCourseButton = findViewById<Button>(R.id.save_course_button)

        fetchInitialData()

        saveCourseButton.setOnClickListener {
            saveCourse()
        }
    }

    private fun fetchInitialData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Fetch Departments
                val departmentsQuery = db.collection("departments").get().await()
                departmentsList = departmentsQuery.documents.mapNotNull { doc ->
                    DepartmentData(doc.id, doc.getString("name"), doc.getString("description"))
                }

                // Fetch Instructors (Faculty)
                val facultyQuery = db.collection("users").whereEqualTo("role", "Faculty").get().await()
                facultyList = facultyQuery.documents.mapNotNull { doc ->
                    val fullName = "${doc.getString("first_name")} ${doc.getString("last_name")}"
                    FacultySpinnerData(doc.id, fullName)
                }

                withContext(Dispatchers.Main) {
                    // Populate Department Spinner
                    val departmentNames = departmentsList.map { it.name }
                    val deptAdapter = ArrayAdapter(this@AddCourseActivity, android.R.layout.simple_spinner_item, departmentNames)
                    deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    departmentSpinner.adapter = deptAdapter

                    // Populate Instructor Spinner
                    val facultyNames = facultyList.map { it.fullName }
                    val facultyAdapter = ArrayAdapter(this@AddCourseActivity, android.R.layout.simple_spinner_item, facultyNames)
                    facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    instructorSpinner.adapter = facultyAdapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddCourseActivity, "Failed to load initial data: ${e.message}", Toast.LENGTH_SHORT).show()
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

        val newCourse = hashMapOf(
            "course_name" to courseName,
            "course_code" to courseCode,
            "department_id" to departmentId,
            "instructor_uid" to instructorUid // Storing instructor UID
        )

        db.collection("courses").add(newCourse)
            .addOnSuccessListener {
                Toast.makeText(this, "Course created successfully!", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create course: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
