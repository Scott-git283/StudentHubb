package com.example.studenth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userRole = intent.getStringExtra("USER_ROLE")
        val username = intent.getStringExtra("USERNAME")

        when (userRole) {
            "Admin" -> setupAdminDashboard(username)
            "Faculty" -> setupFacultyDashboard(username)
            else -> setupStudentDashboard(username)
        }
    }

    private fun setupAdminDashboard(username: String?) {
        setContentView(R.layout.activity_main_admin)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<TextView>(R.id.welcome_message_textview).text = "Welcome, ${username?.split("@")?.get(0) ?: "Admin"}!"

        findViewById<MaterialCardView>(R.id.profile_card).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.manage_users_card).setOnClickListener {
            startActivity(Intent(this, ManageUsersActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.manage_courses_card).setOnClickListener {
            startActivity(Intent(this, ManageCoursesActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.manage_departments_card).setOnClickListener {
            startActivity(Intent(this, ManageDepartmentsActivity::class.java))
        }
    }

    private fun setupFacultyDashboard(username: String?) {
        setContentView(R.layout.activity_main_faculty)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<TextView>(R.id.welcome_message_textview).text = "Welcome, ${username?.split("@")?.get(0) ?: "Faculty"}!"
        
        findViewById<MaterialCardView>(R.id.profile_card).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.view_students_card).setOnClickListener {
            startActivity(Intent(this, ViewStudentsActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.update_records_card).setOnClickListener {
            startActivity(Intent(this, UpdateRecordsActivity::class.java))
        }
         findViewById<MaterialCardView>(R.id.manage_announcements_card).setOnClickListener {
            startActivity(Intent(this, ManageAnnouncementsActivity::class.java))
        }
    }

    private fun setupStudentDashboard(username: String?) {
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<TextView>(R.id.welcome_message_textview).text = "Welcome, ${username?.split("@")?.get(0) ?: "Student"}!"

        findViewById<MaterialCardView>(R.id.profile_card).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.attendance_card).setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.results_card).setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.view_announcements_card).setOnClickListener {
            startActivity(Intent(this, ViewAnnouncementsActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
