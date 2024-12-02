package com.capstone.homeease.controller

import com.capstone.homeease.model.Expert
import com.capstone.homeease.model.ExpertIdResponse
import com.capstone.homeease.model.ExpertRepository
import com.capstone.homeease.network.ApiService
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.network.RetrofitClient
import com.capstone.homeease.view.ExpertView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ExpertController(private val view: ExpertView) {

    private val repository: ExpertRepository = ExpertRepository(
        RetrofitClient.createService(LaravelApi::class.java)
    )
    private val apiService: ApiService = RetrofitClient.apiService
    fun fetchExperts(profession: String) {
        repository.getExperts(profession) { experts, error ->
            if (experts != null) {
                view.showExperts(experts)
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }
    fun getExpertIdByEmail(email: String, callback: (Int?) -> Unit) {
        apiService.getExpertIdByEmail(email).enqueue(object : Callback<ExpertIdResponse> {
            override fun onResponse(call: Call<ExpertIdResponse>, response: Response<ExpertIdResponse>) {
                if (response.isSuccessful) {
                    val expertIdResponse = response.body()
                    if (expertIdResponse != null) {
                        callback(expertIdResponse.id)  // Return the expert ID via callback
                    } else {
                        callback(null)  // Expert not found
                    }
                } else {
                    callback(null)  // Error response
                }
            }

            override fun onFailure(call: Call<ExpertIdResponse>, t: Throwable) {
                callback(null)  // Network failure
            }
        })
    }
}
