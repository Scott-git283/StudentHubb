package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class FacultyData(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String?
)

data class AllFacultyResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<FacultyData>?,
    @SerializedName("message") val message: String?
)
