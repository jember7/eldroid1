package com.capstone.homeease.model

import com.google.gson.annotations.SerializedName

data class Booking(
    var id: String = "",
    @SerializedName("expert_id")var expertId: String = "",
    @SerializedName("expert_name") val expertName: String,
    @SerializedName("user_name") val userName: String,
    var status: String,
    val timestamp: String,
    val note: String,
    val rate: String,
    @SerializedName("expert_address") val expertAddress: String,
    var expertImageUrl: String = "",
    @SerializedName("user_address")var userAddress: String = ""
)

