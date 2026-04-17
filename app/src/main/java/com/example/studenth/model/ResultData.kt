package com.example.studenth.model

import com.google.gson.annotations.SerializedName

data class ResultData(
    @SerializedName("course_name") val courseName: String?,
    @SerializedName("assessment_name") val assessmentName: String?,
    @SerializedName("marks_obtained") val marksObtained: Float?,
    @SerializedName("total_marks") val totalMarks: Float?,
    @SerializedName("grade") val grade: String?
)

data class ResultsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<ResultData>?,
    @SerializedName("message") val message: String?
)
