package com.example.studenth

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore

class EditUserActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var userUid: String = ""
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var lastNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var roleSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reuse the layout from AddUserActivity
        setContentView(R.layout.activity_add_user)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit User" // Set title for editing

        db = FirebaseFirestore.getInstance()
        userUid = intent.getStringExtra("USER_UID") ?: ""

        if (userUid.isEmpty()) {
            Toast.makeText(this, "Error: User UID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        firstNameEditText = findViewById(R.id.first_name_edit_text)
        lastNameEditText = findViewById(R.id.last_name_edit_text)
        emailEditText = findViewById(R.id.email_edit_text)
        roleSpinner = findViewById(R.id.role_spinner)

        // Hide the password field as it's not needed for editing
        findViewById<TextInputLayout>(R.id.password_layout).visibility = View.GONE

        val roles = arrayOf("Student", "Faculty", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        fetchUserDetails()

        val saveButton = findViewById<Button>(R.id.add_user_button) // Use the correct button ID
        saveButton.text = "Save Changes" // Change button text
        saveButton.setOnClickListener {
            updateUser()
        }
    }

    private fun fetchUserDetails() {
        db.collection("users").document(userUid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    firstNameEditText.setText(document.getString("first_name"))
                    lastNameEditText.setText(document.getString("last_name"))
                    emailEditText.setText(document.getString("email"))
                    val role = document.getString("role")
                    val rolePosition = (roleSpinner.adapter as ArrayAdapter<String>).getPosition(role)
                    roleSpinner.setSelection(rolePosition)
                } else {
                    Toast.makeText(this, "User details not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load user details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUser() {
        val updatedUserData = mapOf(
            "first_name" to firstNameEditText.text.toString().trim(),
            "last_name" to lastNameEditText.text.toString().trim(),
            "email" to emailEditText.text.toString().trim(),
            "role" to roleSpinner.selectedItem.toString()
        )

        db.collection("users").document(userUid).update(updatedUserData)
            .addOnSuccessListener {
                Toast.makeText(this, "User updated successfully!", Toast.LENGTH_LONG).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update user: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
