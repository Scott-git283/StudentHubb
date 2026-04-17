package com.example.studenth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.FirebaseResultData
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ResultsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var resultsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        resultsRecyclerView = findViewById(R.id.results_recyclerview)
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchResults()
    }

    private fun fetchResults() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("results")
            .whereEqualTo("student_uid", user.uid)
            .orderBy("course_name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No results found.", Toast.LENGTH_SHORT).show()
                } else {
                    val resultsList = documents.toObjects(FirebaseResultData::class.java)
                    val adapter = ResultsAdapter(resultsList)
                    resultsRecyclerView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to fetch results: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
