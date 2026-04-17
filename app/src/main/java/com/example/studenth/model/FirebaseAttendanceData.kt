package com.example.studenth.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class FirebaseAttendanceData(
    // Map the database field "course_name" to this property
    @get:PropertyName("course_name") @set:PropertyName("course_name") var courseName: String = "",
    
    // Map the database field "attendance_date" to this property
    @get:PropertyName("attendance_date") @set:PropertyName("attendance_date") var attendanceDate: Timestamp? = null,
    
    var status: String = ""
)