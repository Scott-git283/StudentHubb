package com.example.studenth.model

// Represents a student enrolled in a course, combining data from 'users' and 'enrollments' collections.
data class EnrolledStudentData(
    val enrollmentId: String = "", // The Document ID from the 'enrollments' collection
    val studentUid: String = "",   // The Document ID from the 'users' collection
    val firstName: String? = "",
    val lastName: String? = "",
    val studentIdNumber: String? = ""
)