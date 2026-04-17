package com.example.studenth.model

// Re-using a simpler student data model for the faculty's view
data class FacultyStudentData(
    val uid: String = "", // Keep track of the student's UID
    val firstName: String? = "",
    val lastName: String? = "",
    val studentIdNumber: String? = ""
)