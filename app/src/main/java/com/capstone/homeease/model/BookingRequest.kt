package com.capstone.homeease.model


data class BookingRequest(
    val user_id: Int,
    val expert_id: Int?,
    val expert_name: String,
    val user_name: String,
    val status: String,
    val timestamp: String,
    val note: String,
    val rate: String,
    val expert_address: String,
    val user_address: String,
)
data class BookingRequest2(
    val bookings: List<Booking>
)