package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class CourseManagementData(
    @SerializedName("course_id") val courseId: Int,
    @SerializedName("course_code") val courseCode: String?,
    @SerializedName("course_name") val courseName: String?,
    @SerializedName("department_name") val departmentName: String?,
    @SerializedName("instructor_name") val instructorName: String?
)

data class CoursesManagementResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<CourseManagementData>?,
    @SerializedName("message") val message: String?
)
