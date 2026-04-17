package com.example.studenth.model

import com.google.firebase.firestore.PropertyName

data class FirebaseResultData(
    @get:PropertyName("course_name") @set:PropertyName("course_name") var courseName: String = "",
    @get:PropertyName("assessment_name") @set:PropertyName("assessment_name") var assessmentName: String = "",
    @get:PropertyName("marks_obtained") @set:PropertyName("marks_obtained") var marksObtained: Double = 0.0,
    @get:PropertyName("total_marks") @set:PropertyName("total_marks") var totalMarks: Double = 0.0,
    var grade: String = ""
)