package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("student_id_number") val studentIdNumber: String?,
    @SerializedName("department_name") val departmentName: String?
)

data class ProfileResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ProfileData?,
    @SerializedName("message") val message: String? // This field was missing
)
