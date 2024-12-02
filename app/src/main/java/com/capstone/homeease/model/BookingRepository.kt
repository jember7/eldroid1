package com.capstone.homeease.model


import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRepository {

    private val apiService: LaravelApi = RetrofitClient.createService(LaravelApi::class.java)

    fun getBookings(userId: Int, callback: (List<Booking>, String?) -> Unit) {
        apiService.getUserBookings(userId).enqueue(object : Callback<ApiResponse2> {
            override fun onResponse(
                call: Call<ApiResponse2>,
                response: Response<ApiResponse2>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // Directly use the list of bookings from the response
                    val bookings = response.body()!!.data
                    callback(bookings, null)
                } else {
                    callback(emptyList(), "Failed to fetch bookings: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse2>, t: Throwable) {
                callback(emptyList(), "Error: ${t.message}")
            }
        })
    }


}
