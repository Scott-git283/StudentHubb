package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.AdminUserData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var usersRecyclerView: RecyclerView

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        // Refresh the list when returning from Add/Edit activity
        fetchUsers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        usersRecyclerView = findViewById(R.id.users_recyclerview)
        usersRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<FloatingActionButton>(R.id.add_user_fab).setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            activityLauncher.launch(intent)
        }

        fetchUsers()
    }

    private fun fetchUsers() {
        db.collection("users").get()
            .addOnSuccessListener { documents ->
                val userList = documents.map { doc ->
                    doc.toObject(AdminUserData::class.java).apply { uid = doc.id }
                }
                val adapter = ManageUsersAdapter(userList, { user -> editUser(user) }, { user -> showDeleteConfirmation(user) })
                usersRecyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editUser(user: AdminUserData) {
        val intent = Intent(this, EditUserActivity::class.java).apply {
            putExtra("USER_UID", user.uid)
        }
        activityLauncher.launch(intent)
    }

    private fun showDeleteConfirmation(user: AdminUserData) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.firstName} ${user.lastName}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> deleteUser(user) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUser(user: AdminUserData) {
        // This is a complex operation and requires re-authentication for security reasons.
        // The simplest approach for an admin panel is to use a server-side function (Cloud Function).
        // A client-side-only delete is not recommended for production apps but is included here for completeness.

        // Step 1: Delete from Firestore
        db.collection("users").document(user.uid).delete()
            .addOnSuccessListener {
                // Step 2: Delete from Firebase Auth. This is the tricky part.
                // For security, Firebase Admin SDK is required. A Cloud Function is the standard way to do this.
                // We will simulate the deletion for now and assume it works.
                Toast.makeText(this, "User data deleted. Auth user must be deleted from console or via a Cloud Function.", Toast.LENGTH_LONG).show()
                fetchUsers() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
