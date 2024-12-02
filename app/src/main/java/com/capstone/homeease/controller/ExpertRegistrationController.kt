package com.capstone.homeease.controller

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.capstone.homeease.model.Expert
import com.capstone.homeease.utils.toMediaType
import com.capstone.homeease.view.ExpertRegistrationView
import okhttp3.*
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*

class ExpertRegistrationController(private val view: ExpertRegistrationView, private val context: Context) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Make the network call in the background
    fun registerExpert(expert: Expert) {
        // Create a CoroutineScope for background tasks
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = performExpertRegistration(expert)
                // Once the network request is done, post results back to the main thread
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        view.onSuccess("Expert registration successful")
                    } else {
                        view.onFailure(response.message)
                    }
                }
            } catch (e: Exception) {
                // Handle errors in the background thread
                withContext(Dispatchers.Main) {
                    view.onFailure("Error: ${e.message}")
                }
            }
        }
    }

    private fun performExpertRegistration(expert: Expert): Response {
        val url = "http://10.0.2.2:8000/api/register/expert"
        val formBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("full_name", expert.fullName)
            .addFormDataPart("email", expert.email)
            .addFormDataPart("password", expert.password)
            .addFormDataPart("password_confirmation", expert.passwordConfirmation)
            .addFormDataPart("profession", expert.profession)
            .addFormDataPart("date_of_birth", expert.dateOfBirth)
            .addFormDataPart("address", expert.address)
            .addFormDataPart("phone_number", expert.number)
            .addFormDataPart("role", expert.role)

        expert.profileImage?.let { uri ->
            val file = getFileFromUri(uri)
            file?.let {
                val mediaType = "image/*".toMediaType()
                val requestBody = RequestBody.create(mediaType, it)
                formBodyBuilder.addFormDataPart("profile_picture", it.name, requestBody)
            }
        }

        val requestBody = formBodyBuilder.build()
        val request = Request.Builder().url(url).post(requestBody).build()

        val response = client.newCall(request).execute()

        // Log the response body and status code for debugging
        Log.d("ExpertRegistration", "Response Code: ${response.code}")
        Log.d("ExpertRegistration", "Response Body: ${response.body?.string()}")

        return response
    }

    // Helper function to get file from URI (e.g., profile image)
    private fun getFileFromUri(uri: Uri): File? {
        val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                val filePath = it.getString(columnIndex)
                return File(filePath)
            }
        }
        return null
    }
}
