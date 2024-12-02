package com.capstone.homeease.model

import com.capstone.homeease.network.LaravelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpertRepository(private val apiService: LaravelApi) {

    fun getExperts(profession: String, callback: (List<Expert>?, String?) -> Unit) {
        apiService.getExpertsByProfession(profession).enqueue(object : Callback<List<Expert>> {
            override fun onResponse(
                call: Call<List<Expert>>,
                response: Response<List<Expert>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Failed to fetch experts: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Expert>>, t: Throwable) {
                callback(null, t.message)
            }
        })
    }
}
