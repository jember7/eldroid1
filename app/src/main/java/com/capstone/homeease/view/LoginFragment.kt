package com.capstone.homeease.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentLoginBinding
import com.capstone.homeease.model.ExpertProfileResponse
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.model.LoginRequest
import com.capstone.homeease.model.LoginResponse
import com.capstone.homeease.utils.SharedPreferencesHelper
import com.capstone.homeease.utils.SharedPreferencesHelper.saveUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)

        binding.submitButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email and Password must not be empty", Toast.LENGTH_SHORT).show()
            } else {
                binding.progressBar.visibility = View.VISIBLE // Show progress bar
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")  // Your Laravel API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)
        val loginRequest = LoginRequest(email, password)

        api.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE // Hide progress bar after response

                if (response.isSuccessful) {
                    val role = response.body()?.role ?: "Unknown"
                    val user = response.body()
                    val userId = user?.user_id ?: return
                    val userName = user?.fullName?: ""
                    // Save user ID in SharedPreferences
                    saveUserId(requireContext(), userId)
                    SharedPreferencesHelper.saveUserNameToSession(requireContext(),userName)
                    SharedPreferencesHelper.saveUserIdToSession(requireContext(), userId)
                    // Log the full response for debugging
                    Log.d("LoginFragment", "Login successful. User role: $role User ID: $userId")

                    // Check if the user is an expert
                    if (role == "expert") {
                        // Fetch expert profile after successful login
                        fetchExpertProfile(userId, role)
                    } else {
                        // Navigate to user dashboard
                        redirectToDashboard(role)
                    }
                } else {
                    Log.e("LoginFragment", "Authentication failed. Response: ${response.body()?.toString()}")
                    Toast.makeText(requireContext(), "Authentication failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE // Hide progress bar after failure
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchExpertProfile(userId: Int, role: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")  // Ensure this matches your API endpoint
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)

        // Log userId to check if it's being passed correctly
        Log.d("LoginFragment", "Fetching expert profile for userId: $userId")

        api.getExpertProfile(userId).enqueue(object : Callback<ExpertProfileResponse> {
            override fun onResponse(
                call: Call<ExpertProfileResponse>,
                response: Response<ExpertProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val expert = response.body()
                    val expertId = expert?.id ?: 0  // userId maps to id in the API response

                    // Log expert profile data for debugging
                    Log.d("LoginFragment", "Expert profile fetched successfully. Expert ID: $expertId")

                    // Check if expertId is valid (non-zero)
                    if (expertId == 0) {
                        Log.e("LoginFragment", "Invalid Expert ID: $expertId")
                        Toast.makeText(requireContext(), "Failed to retrieve valid expert profile", Toast.LENGTH_SHORT).show()
                        return
                    }

                    // Save expertId in SharedPreferences
                    saveUserId(requireContext(), expertId)

                    // Navigate to expert dashboard
                    redirectToDashboard("expert")
                } else {
                    Log.e("LoginFragment", "Failed to fetch expert profile. Response: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to fetch expert profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExpertProfileResponse>, t: Throwable) {
                Log.e("LoginFragment", "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun redirectToDashboard(role: String) {
        Log.d("LoginFragment", "Redirecting to dashboard with role: $role")
        when (role) {
            "user" -> {
                Log.d("LoginFragment", "Navigating to User Dashboard")
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, UserDashBoardFragment())
                    .commitAllowingStateLoss()  // Use commitAllowingStateLoss to prevent possible fragment issues
            }
            "expert" -> {
                Log.d("LoginFragment", "Navigating to Expert Dashboard")
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ExpertDashBoardFragment())
                    .commitAllowingStateLoss()  // Ensure fragment is committed even if state is lost
            }
            else -> {
                Toast.makeText(requireContext(), "Unknown role", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
