package com.example.studenth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var profileImageView: CircleImageView

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission denied to read storage.", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let {
                uploadImageToFirebase(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        profileImageView = findViewById(R.id.profile_image)

        findViewById<FloatingActionButton>(R.id.edit_profile_image_fab).setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        fetchUserProfile()
    }

    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val user = auth.currentUser ?: return
        val storageRef = storage.reference.child("profile_images/${user.uid}.jpg")

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()

        // Use a more robust nested listener approach to prevent race conditions
        storageRef.putFile(imageUri).addOnSuccessListener { 
            // On successful upload, THEN get the download URL
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                saveImageUrlToFirestore(downloadUri.toString())
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid)
            .update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile image updated!", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(imageUrl).placeholder(R.drawable.ic_profile_placeholder).into(profileImageView)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save image URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchUserProfile() {
        val progressBar = findViewById<ProgressBar>(R.id.profile_progressbar)
        val contentScrollView = findViewById<ScrollView>(R.id.content_scrollview)
        progressBar.visibility = View.VISIBLE
        contentScrollView.visibility = View.GONE

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("first_name") ?: ""
                    val lastName = document.getString("last_name") ?: ""
                    val email = user.email ?: ""
                    val role = document.getString("role") ?: ""
                    val studentId = document.getString("student_id_number")
                    val officeNumber = document.getString("office_number")
                    val departmentId = document.getString("department_id")
                    val profileImageUrl = document.getString("profileImageUrl")

                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(profileImageUrl).placeholder(R.drawable.ic_profile_placeholder).into(profileImageView)
                    }

                    fetchDepartmentName(departmentId) { departmentName ->
                        populateUI(firstName, lastName, email, role, studentId, officeNumber, departmentName)
                        progressBar.visibility = View.GONE
                        contentScrollView.visibility = View.VISIBLE
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "User profile data not found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to fetch profile: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchDepartmentName(departmentId: String?, onComplete: (String) -> Unit) {
        if (departmentId.isNullOrEmpty()) {
            onComplete("N/A")
            return
        }

        db.collection("departments").document(departmentId).get()
            .addOnSuccessListener { document ->
                onComplete(document?.getString("name") ?: "N/A")
            }
            .addOnFailureListener {
                onComplete("Unknown")
            }
    }

    private fun populateUI(firstName: String, lastName: String, email: String, role: String, studentId: String?, officeNumber: String?, departmentName: String) {
        findViewById<TextView>(R.id.profile_name).text = "$firstName $lastName"
        findViewById<TextView>(R.id.profile_email).text = email
        findViewById<TextView>(R.id.profile_role).text = role
        findViewById<TextView>(R.id.profile_department).text = departmentName

        val studentIdContainer = findViewById<LinearLayout>(R.id.student_id_container)
        val officeNumberContainer = findViewById<LinearLayout>(R.id.office_number_container)

        studentIdContainer.visibility = View.GONE
        officeNumberContainer.visibility = View.GONE

        if (role == "Student" && !studentId.isNullOrEmpty()) {
            findViewById<TextView>(R.id.profile_student_id).text = studentId
            studentIdContainer.visibility = View.VISIBLE
        } else if (role == "Faculty" && !officeNumber.isNullOrEmpty()) {
            findViewById<TextView>(R.id.profile_office).text = officeNumber
            officeNumberContainer.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
