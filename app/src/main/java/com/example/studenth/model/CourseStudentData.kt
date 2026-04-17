package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class CourseStudentData(
    @SerializedName("enrollment_id") val enrollmentId: Int,
    @SerializedName("student_db_id") val studentDbId: Int,
    @SerializedName("username") val username: String?,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("student_id_number") val studentIdNumber: String?
)

data class CourseStudentsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<CourseStudentData>?,
    @SerializedName("message") val message: String?
)
