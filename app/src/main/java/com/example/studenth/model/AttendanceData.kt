package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class AttendanceData(
    @SerializedName("course_name") val courseName: String?,
    @SerializedName("course_code") val courseCode: String?,
    @SerializedName("classes_attended") val classesAttended: Int?,
    @SerializedName("total_classes") val totalClasses: Int?
)

data class AttendanceResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<AttendanceData>?,
    @SerializedName("message") val message: String?
)
