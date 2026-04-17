package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<TextInputEditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password_edit_text)
        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            fetchUserRoleAndProceed()
                        } else {
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUserRoleAndProceed() {
        val user = auth.currentUser ?: return

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userRole = document.getString("role")
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_ROLE", userRole)
                        putExtra("USERNAME", user.email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to fetch user role.", Toast.LENGTH_SHORT).show()
                    auth.signOut() // Sign out if role cannot be determined
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch user role: ${exception.message}", Toast.LENGTH_SHORT).show()
                auth.signOut()
            }
    }
}
