package com.capstone.homeease.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    // Add this to your Retrofit setup for logging
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Configure Gson for lenient parsing
    private val gson = GsonBuilder().setLenient().create()

    // Set up OkHttpClient with the logging interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Retrofit instance with GsonConverterFactory and logging
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient) // Use OkHttpClient with logging
        .addConverterFactory(GsonConverterFactory.create(gson))  // Use Gson with lenient parsing
        .build()

    // Lazy initialization of ApiService
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Function to create any service dynamically
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
