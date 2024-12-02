package com.capstone.homeease.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentEditUserProfileBinding
import com.capstone.homeease.model.LoginResponse
import com.capstone.homeease.model.LoginResponse2
import com.capstone.homeease.network.ApiService
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.network.RetrofitClient
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class EditUserProfileFragment : Fragment(R.layout.fragment_edit_user_profile) {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var saveButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fullNameEditText = view.findViewById(R.id.fullNameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        addressEditText = view.findViewById(R.id.addressEditText)
        numberEditText = view.findViewById(R.id.numberEditText)
        saveButton = view.findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            saveUserProfile()
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = getUserId(requireContext())
        val apiService = RetrofitClient.createService(ApiService::class.java)
        val call = apiService.getUserProfile(userId)
        call.enqueue(object : Callback<LoginResponse2> {
            override fun onResponse(call: Call<LoginResponse2>, response: Response<LoginResponse2>) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    Log.d("LoadUserProfile", "User profile: $userProfile")  // Log the response body

                    userProfile?.let {
                        fullNameEditText.setText(it.fullName)
                        emailEditText.setText(it.email)
                        addressEditText.setText(it.address)
                        numberEditText.setText(it.number.toString())
                    }
                } else {
                    Log.e("LoadUserProfile", "Response failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse2>, t: Throwable) {
                Log.e("LoadUserProfile", "Error fetching profile", t)
            }
        })
    }


    private fun saveUserProfile() {
        // Get the user ID
        val userId = getUserId(requireContext())

        // Get the values from the EditText fields
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val number = numberEditText.text.toString().trim()

        // Log the data for debugging purposes
        Log.d("SaveUserProfile", "userId: $userId, fullName: $fullName, email: $email, address: $address, number: $number")

        // Validate input fields to ensure they are not empty (you can also add specific validation if needed)
        if (fullName.isEmpty() || email.isEmpty() || address.isEmpty() || number.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        // Make the API call to update the profile
        val apiService = RetrofitClient.createService(ApiService::class.java)
        val call = apiService.updateUserProfile(userId.toString(), fullName, email, address, number)

        // Make the network request
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {

                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()

                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    val profileFragment = ProfileFragment()
                    fragmentTransaction.replace(R.id.fragmentContainer, profileFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else {
                    // If the response is unsuccessful, log the error
                    Log.e("SaveUserProfile", "Error updating profile: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Log the error and show a failure message
                Log.e("SaveUserProfile", "Network error: ${t.message}", t)
                Toast.makeText(requireContext(), "Failed to update profile: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}