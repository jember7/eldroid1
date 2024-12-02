package com.capstone.homeease.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val message: String,
    val role: String,
    val user_id: Int,
    val address: String?,
    val email: String?,
    val number: Number?,
    val fullName: String?,
    val profileImage: String?
)
data class LoginResponse2(
    @SerializedName("id") val userId: Int,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone_number") val number: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("profile_picture") val profilePicture: String?,
    @SerializedName("role") val role: String?,
)

