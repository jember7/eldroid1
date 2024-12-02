package com.capstone.homeease.model

import android.net.Uri
import com.google.gson.annotations.SerializedName





data class ExpertProfileResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("profession") val profession: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("role") val role: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class Expert(
    @SerializedName("full_name")val fullName: String,
    val email: String,
    val password: String,
    val passwordConfirmation: String,
    val profession: String,
    val dateOfBirth: String,
    val address: String,
    val number: String,
    val profileImage: Uri? = null,
    val role: String = "expert" // Default role is expert
)

