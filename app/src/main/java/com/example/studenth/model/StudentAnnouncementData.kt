package com.example.studenth.model

import com.google.firebase.Timestamp

// Represents a single announcement item in the student's view
data class StudentAnnouncementData(
    val title: String? = "",
    val message: String? = "",
    val courseName: String? = "",    // Fetched from 'courses' collection
    val facultyName: String? = "",   // Fetched from 'users' collection
    val createdAt: Timestamp? = null
)