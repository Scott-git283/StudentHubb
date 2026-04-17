package com.example.studenth.model

import com.google.firebase.Timestamp

// Represents a single announcement item in the faculty's management list
data class ManagedAnnouncement(
    val id: String = "", // The document ID from Firestore
    val title: String? = "",
    val message: String? = "",
    val courseName: String? = "", // Fetched separately from the 'courses' collection
    val createdAt: Timestamp? = null
)