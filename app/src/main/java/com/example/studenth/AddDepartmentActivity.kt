package com.example.studenth

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class AddDepartmentActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_department)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<TextInputEditText>(R.id.department_name_edittext)
        val descriptionEditText = findViewById<TextInputEditText>(R.id.department_description_edittext)
        val addButton = findViewById<Button>(R.id.add_department_button)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            if (name.isNotEmpty()) {
                addDepartment(name, description)
            } else {
                Toast.makeText(this, "Department name cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addDepartment(name: String, description: String) {
        val department = hashMapOf(
            "name" to name,
            "description" to description
        )

        db.collection("departments").add(department)
            .addOnSuccessListener {
                Toast.makeText(this, "Department added successfully!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding department: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
