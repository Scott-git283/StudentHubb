package com.example.studenth.model

import com.google.firebase.firestore.PropertyName

// This class now represents a course document in Firestore
data class FacultyCourseData(
    var courseId: String = "", // Will be populated with the document ID

    @get:PropertyName("course_code") @set:PropertyName("course_code") var courseCode: String? = "",
    @get:PropertyName("course_name") @set:PropertyName("course_name") var courseName: String? = ""
)