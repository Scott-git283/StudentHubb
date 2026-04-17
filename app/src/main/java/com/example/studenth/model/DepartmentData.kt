package com.example.studenth.model

// Represents a single department document in Firestore
data class DepartmentData(
    var id: String = "", // The document ID from Firestore
    var name: String? = "",
    var description: String? = ""
)