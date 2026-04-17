package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class UnenrolledStudentData(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("student_id_number") val studentIdNumber: String?
)

data class UnenrolledStudentsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<UnenrolledStudentData>?,
    @SerializedName("message") val message: String?
)
