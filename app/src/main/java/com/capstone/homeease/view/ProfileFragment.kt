package com.capstone.homeease.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentProfileBinding
import com.capstone.homeease.model.LoginResponse
import com.capstone.homeease.model.LoginResponse2
import com.capstone.homeease.network.ApiService
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.network.RetrofitClient
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var apiService: ApiService // API service for backend communication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize API Service (Retrofit)
        apiService = RetrofitClient.createService(ApiService::class.java) // You need to implement ApiClient for Retrofit

        // Set click listeners using ViewBinding
        binding.activity.setOnClickListener {
            navigateToActivityPage()
        }
        binding.payment.setOnClickListener {
            navigateToPaymentPage()
        }
        binding.textHome.setOnClickListener {
            navigateToUserDashboard()
        }
        binding.messages.setOnClickListener {

        }
        binding.profile.setOnClickListener {
            navigateToProfilePage()
        }
        binding.logoutText.setOnClickListener { logout() }
        binding.editProfile.setOnClickListener { navigateToEditProfile() }
        binding.manageLinkedBankText.setOnClickListener { navigateToPaymentPage() }
        binding.manageRewardsText.setOnClickListener { navigateToRewardsPage() }
        binding.changePassword.setOnClickListener { navigateToChangePassword() }
        binding.bookingHistoryText.setOnClickListener { navigateToActivityPage() }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Fetch user data when the fragment starts
        fetchUserData()
    }

    private fun fetchUserData() {
        val userId = getUserId(requireContext())
        Log.d("UserDashBoardFragment", "User ID: $userId")
        if (userId == -1) {
            Log.e("UserDashBoardFragment", "User ID not found")
            return
        }
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)

        api.getUserProfile2(userId).enqueue(object : Callback<LoginResponse2> {
            override fun onResponse(call: Call<LoginResponse2>, response: Response<LoginResponse2>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    binding.usernameText.text = user?.fullName ?: "No name available"
                    binding.numberText.text = user?.number.toString() ?: "No name available"
                    Log.d("UserProfile", "Address: ${user?.address}")
                } else {
                    Log.e("UserDashBoardFragment", "Failed to load profile. Response code: ${response.code()}")
                    Log.e("UserDashBoardFragment", "Error message: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse2>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToActivityPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ActivityPageFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToPaymentPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PaymentFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToUserDashboard() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, UserDashBoardFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToMessagesPage() {
        // Navigation logic
    }

    private fun navigateToProfilePage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun logout() {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clears all stored data
        editor.apply()

        // Ensure no fragment is still active in the back stack
        parentFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)


        val loginFragment = LoginFragment()


        if (isAdded) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, loginFragment)
                .commit()
        } else {

        }
    }




    private fun navigateToEditProfile() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, EditUserProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToRewardsPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,RewardsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToChangePassword() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,ChangePasswordFragment())
            .addToBackStack(null)
            .commit()
    }
}
