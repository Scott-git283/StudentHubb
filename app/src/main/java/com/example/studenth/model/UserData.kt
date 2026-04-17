package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("full_name") val fullName: String?,
    // Fields for the edit screen
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("department_id") val departmentId: Int?,
    @SerializedName("student_id_number") val studentIdNumber: String?
)

data class UsersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<UserData>?,
    @SerializedName("message") val message: String?
)

// New response class for fetching a single user's details
data class UserDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserData?,
    @SerializedName("message") val message: String?
)
