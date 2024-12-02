package com.capstone.homeease.network


import com.capstone.homeease.model.ApiResponse
import com.capstone.homeease.model.ChangePasswordRequest
import com.capstone.homeease.model.ExpertIdResponse
import com.capstone.homeease.model.LoginResponse
import com.capstone.homeease.model.LoginResponse2

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @Multipart
    @POST("register/expert")
    fun registerExpertWithImage(
        @PartMap fields: HashMap<String, String>,
        @Part image: MultipartBody.Part?
    ): Call<ResponseBody>

    @GET("expert/by-email/{email}")
    fun getExpertIdByEmail(
        @Query("email") email: String
    ): Call<ExpertIdResponse>

    @GET("user-profile")
    fun getUserProfile(
        @Query("id") userId: Int // Use @Query to pass the userId as a query parameter
    ): Call<LoginResponse2>

    @FormUrlEncoded
    @PUT("/api/user/{userId}")
    fun updateUserProfile(
        @Path("userId") userId: String,
        @Field("full_name") fullName: String, // Use "full_name" to match the API field
        @Field("email") email: String,
        @Field("address") address: String,
        @Field("phone_number") number: String
    ): Call<Void>
    @POST("change-password/{userId}")
    fun changePassword(
        @Path("userId") userId: String,
        @Body request: ChangePasswordRequest
    ): Call<ApiResponse>


}

