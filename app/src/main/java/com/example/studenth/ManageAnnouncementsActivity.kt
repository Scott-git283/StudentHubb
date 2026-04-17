package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.ManagedAnnouncement
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ManageAnnouncementsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var announcementsRecyclerView: RecyclerView

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            fetchFacultyAnnouncements()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_announcements)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        announcementsRecyclerView = findViewById(R.id.announcements_recyclerview)
        announcementsRecyclerView.layoutManager = LinearLayoutManager(this)

        val addAnnouncementFab = findViewById<FloatingActionButton>(R.id.add_announcement_fab)
        addAnnouncementFab.setOnClickListener {
            val intent = Intent(this, PostAnnouncementActivity::class.java)
            activityLauncher.launch(intent)
        }

        fetchFacultyAnnouncements()
    }

    private fun fetchFacultyAnnouncements() {
        val facultyUid = auth.currentUser?.uid
        if (facultyUid == null) {
            Toast.makeText(this, "Faculty user not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val announcementsQuery = db.collection("announcements")
                    .whereEqualTo("faculty_uid", facultyUid)
                    .orderBy("created_at", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val announcementsList = mutableListOf<ManagedAnnouncement>()
                for (doc in announcementsQuery.documents) {
                    val announcement = doc.toObject<ManagedAnnouncement>()?.copy(id = doc.id)
                    if (announcement != null) {
                        // Fetch course name separately
                        val courseId = doc.getString("course_id")
                        if (courseId != null) {
                            val courseDoc = db.collection("courses").document(courseId).get().await()
                            val courseName = courseDoc.getString("course_name")
                            announcementsList.add(announcement.copy(courseName = courseName))
                        } else {
                            announcementsList.add(announcement)
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    if (announcementsList.isNotEmpty()) {
                        val adapter = ManageAnnouncementsAdapter(announcementsList, { ann -> editAnnouncement(ann) }, { ann -> showDeleteConfirmationDialog(ann) })
                        announcementsRecyclerView.adapter = adapter
                    } else {
                        announcementsRecyclerView.adapter = null
                        Toast.makeText(this@ManageAnnouncementsActivity, "No announcements found.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ManageAnnouncements", "Error fetching announcements", e)
                    Toast.makeText(this@ManageAnnouncementsActivity, "Failed to fetch announcements: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun editAnnouncement(announcement: ManagedAnnouncement) {
        val intent = Intent(this, EditAnnouncementActivity::class.java).apply {
            putExtra("ANNOUNCEMENT_ID", announcement.id)
        }
        activityLauncher.launch(intent)
    }

    private fun showDeleteConfirmationDialog(announcement: ManagedAnnouncement) {
        AlertDialog.Builder(this)
            .setTitle("Delete Announcement")
            .setMessage("Are you sure you want to delete this announcement?")
            .setPositiveButton("Delete") { _, _ -> deleteAnnouncement(announcement.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAnnouncement(announcementId: String) {
        db.collection("announcements").document(announcementId).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Announcement deleted successfully.", Toast.LENGTH_SHORT).show()
                fetchFacultyAnnouncements() // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete announcement: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
