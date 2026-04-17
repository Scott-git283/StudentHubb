package com.example.studenth.model

// Represents a single course item in the admin's management list
data class AdminCourseData(
    val id: String = "", // The document ID from the 'courses' collection
    val courseName: String? = "",
    val courseCode: String? = "",
    val departmentName: String? = "", // Fetched from 'departments' collection
    val instructorName: String? = ""   // Fetched from 'users' collection
)