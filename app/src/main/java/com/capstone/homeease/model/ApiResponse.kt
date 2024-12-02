package com.capstone.homeease.model

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: BookingRequest
)

data class ExpertIdResponse(
    val id: Int,
    val message: String
)
data class ApiResponse2(
    val status: String,
    val message: String,
    val data: List<Booking>
)
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)
