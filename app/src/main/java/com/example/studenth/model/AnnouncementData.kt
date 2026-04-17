package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class AnnouncementData(
    @SerializedName("title") val title: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("course_name") val courseName: String?,
    @SerializedName("faculty_name") val facultyName: String?
)

data class AnnouncementsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<AnnouncementData>?,
    @SerializedName("message") val message: String?
)
