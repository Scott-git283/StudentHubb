package com.example.studenth

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val roleSpinner = findViewById<Spinner>(R.id.role_spinner)
        val roles = arrayOf("Student", "Faculty", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        val addUserButton = findViewById<Button>(R.id.add_user_button)
        addUserButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val email = findViewById<TextInputEditText>(R.id.email_edit_text).text.toString().trim()
        val password = findViewById<TextInputEditText>(R.id.password_edit_text).text.toString().trim()
        val firstName = findViewById<TextInputEditText>(R.id.first_name_edit_text).text.toString().trim()
        val lastName = findViewById<TextInputEditText>(R.id.last_name_edit_text).text.toString().trim()
        val role = findViewById<Spinner>(R.id.role_spinner).selectedItem.toString()

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Step 1: Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Step 2: If auth is successful, save user data to Firestore
                    val firebaseUser = task.result?.user
                    if (firebaseUser != null) {
                        saveUserDataToFirestore(firebaseUser.uid, email, firstName, lastName, role)
                    } else {
                        Toast.makeText(this, "Failed to get user session.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserDataToFirestore(uid: String, email: String, firstName: String, lastName: String, role: String) {
        val userData = hashMapOf(
            "email" to email,
            "first_name" to firstName,
            "last_name" to lastName,
            "role" to role
            // Add other role-specific fields as needed, e.g., student_id_number
        )

        db.collection("users").document(uid).set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User created successfully!", Toast.LENGTH_LONG).show()
                finish() // Go back to the user list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
