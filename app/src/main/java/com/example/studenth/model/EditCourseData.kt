package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class EditCourseData(
    @SerializedName("course_name") val courseName: String?,
    @SerializedName("course_code") val courseCode: String?,
    @SerializedName("department_id") val departmentId: Int?,
    @SerializedName("instructor_id") val instructorId: Int?
)

data class CourseDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: EditCourseData?,
    @SerializedName("message") val message: String?
)
