package com.example.studenth

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class EditDepartmentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var departmentId: String = ""
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reuse the layout from AddDepartmentActivity
        setContentView(R.layout.activity_add_department)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Department" // Set title for editing

        db = FirebaseFirestore.getInstance()
        departmentId = intent.getStringExtra("DEPARTMENT_ID") ?: ""

        if (departmentId.isEmpty()) {
            Toast.makeText(this, "Error: Department ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        nameEditText = findViewById(R.id.department_name_edittext)
        descriptionEditText = findViewById(R.id.department_description_edittext)
        // Use the correct button ID from the reused layout
        val saveButton = findViewById<Button>(R.id.add_department_button)
        saveButton.text = "Save Changes" // Change button text for editing

        fetchDepartmentDetails()

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            if (name.isNotEmpty()) {
                updateDepartment(name, description)
            } else {
                Toast.makeText(this, "Department name cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDepartmentDetails() {
        db.collection("departments").document(departmentId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    nameEditText.setText(document.getString("name"))
                    descriptionEditText.setText(document.getString("description"))
                } else {
                    Toast.makeText(this, "Department not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load department details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDepartment(name: String, description: String) {
        val departmentData = mapOf(
            "name" to name,
            "description" to description
        )

        db.collection("departments").document(departmentId).update(departmentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Department updated successfully!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update department: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
