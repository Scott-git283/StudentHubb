package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class FacultyAnnouncementData(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("course_name") val courseName: String?
)

data class FacultyAnnouncementsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<FacultyAnnouncementData>?,
    @SerializedName("message") val message: String?
)

data class AnnouncementDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: AnnouncementDetails?,
    @SerializedName("message") val message: String?
)
