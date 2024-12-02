package com.capstone.homeease.network

import com.capstone.homeease.model.ApiResponse
import com.capstone.homeease.model.ApiResponse2
import com.capstone.homeease.model.Booking
import com.capstone.homeease.model.BookingRequest
import com.capstone.homeease.model.BookingRequest2
import com.capstone.homeease.model.Expert
import com.capstone.homeease.model.ExpertIdResponse
import com.capstone.homeease.model.ExpertProfileResponse
import com.capstone.homeease.model.LoginRequest
import com.capstone.homeease.model.LoginResponse
import com.capstone.homeease.model.LoginResponse2
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LaravelApi {
    @POST("login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>
    @GET("user-profile")
    fun getUserProfile(
        @Query("id") userId: Int // Use @Query to pass the userId as a query parameter
    ): Call<LoginResponse>
    @GET("user-profile2")
    fun getUserProfile2(
        @Query("id") userId: Int // Use @Query to pass the userId as a query parameter
    ): Call<LoginResponse2>
    @GET("expert-profile")
    fun getExpertProfile(@Query("userId") userId: Int): Call<ExpertProfileResponse>
    @GET("experts")
    fun getExpertsByProfession(@Query("profession") profession: String): Call<List<Expert>>
    @POST("bookings")
    fun bookExpert(@Body bookingRequest: BookingRequest): Call<ApiResponse>

    @GET("bookings/expert/{expertId}")
    fun getExpertBookings(@Path("expertId") expertId: Int): Call<ApiResponse2>
    @PUT("bookings/{id}/accept")
    fun acceptBooking(@Path("id") bookingId: Int): Call<ApiResponse2>
    @PUT("bookings/{id}/decline")
    fun declineBooking(@Path("id") bookingId: Int): Call<ApiResponse2>


    @GET("bookings/user/{userId}")  //
    fun getUserBookings(@Path("userId") userId: Int): Call<ApiResponse2>
    @PUT("bookings/{id}/status")
    fun updateBookingStatus(
        @Path("id") id: Int // Booking ID
    ): Call<ApiResponse> // The response object



    @GET("expert/{userId}/ongoing-bookings")
    fun getOngoingBookings(@Path("userId") userId: Int): Call<List<Booking>>


}
