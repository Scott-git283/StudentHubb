package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.DepartmentData
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ManageDepartmentsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var departmentsRecyclerView: RecyclerView

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchDepartments()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_departments)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()

        departmentsRecyclerView = findViewById(R.id.departments_recyclerview)
        departmentsRecyclerView.layoutManager = LinearLayoutManager(this)

        val addDepartmentFab = findViewById<FloatingActionButton>(R.id.add_department_fab)
        addDepartmentFab.setOnClickListener {
            val intent = Intent(this, AddDepartmentActivity::class.java)
            activityLauncher.launch(intent)
        }

        fetchDepartments()
    }

    private fun fetchDepartments() {
        db.collection("departments").get()
            .addOnSuccessListener { documents ->
                val departmentList = documents.map { doc ->
                    DepartmentData(doc.id, doc.getString("name"), doc.getString("description"))
                }
                val adapter = ManageDepartmentsAdapter(departmentList, { dept -> editDepartment(dept) }, { dept -> showDeleteConfirmation(dept) })
                departmentsRecyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch departments: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editDepartment(department: DepartmentData) {
        val intent = Intent(this, EditDepartmentActivity::class.java).apply {
            putExtra("DEPARTMENT_ID", department.id)
        }
        activityLauncher.launch(intent)
    }

    private fun showDeleteConfirmation(department: DepartmentData) {
        AlertDialog.Builder(this)
            .setTitle("Delete Department")
            .setMessage("Are you sure you want to delete the '${department.name}' department?")
            .setPositiveButton("Delete") { _, _ -> deleteDepartment(department.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteDepartment(departmentId: String) {
        db.collection("departments").document(departmentId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Department deleted successfully.", Toast.LENGTH_SHORT).show()
                fetchDepartments() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete department: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
