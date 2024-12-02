package com.capstone.homeease.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentProfileBinding
import com.capstone.homeease.model.ExpertProfileResponse
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.network.RetrofitClient
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExpertProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var apiService: LaravelApi // API service for backend communication

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize API Service (Retrofit)
        apiService = RetrofitClient.createService(LaravelApi::class.java)

        // Set click listeners using ViewBinding
        binding.activity.setOnClickListener {
            navigateToActivityPage()
        }
        binding.payment.setOnClickListener {
            navigateToPaymentPage()
        }
        binding.textHome.setOnClickListener {
            navigateToExpertDashboard()
        }
        binding.messages.setOnClickListener {
            // Navigation logic (if needed)
        }
        binding.profile.setOnClickListener {
            navigateToExpertProfilePage()
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
        // Fetch expert data when the fragment starts
        fetchExpertData()
    }

    private fun fetchExpertData() {
        val expertId = getUserId(requireContext())  // Get the expert's ID from shared preferences
        Log.d("ExpertProfileFragment", "Expert ID: $expertId")
        if (expertId == -1) {
            Log.e("ExpertProfileFragment", "Expert ID not found")
            return
        }

        // Retrofit call to fetch expert profile data
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") // Update to your actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)

        api.getExpertProfile(expertId).enqueue(object : Callback<ExpertProfileResponse> {
            override fun onResponse(call: Call<ExpertProfileResponse>, response: Response<ExpertProfileResponse>) {
                if (response.isSuccessful) {
                    val expert = response.body()
                    expert?.let {
                        // Use data binding to set values in the UI
                        binding.usernameText.text = it.fullName ?: "No name available"
                        binding.numberText.text = it.phoneNumber.toString() ?: "No contact number available"

                    }
                    Log.d("ExpertProfile", "Expert Profile: ${expert?.fullName}")
                } else {
                    Log.e("ExpertProfileFragment", "Failed to load profile. Response code: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExpertProfileResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToActivityPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExpertActivityFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToPaymentPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExpertPaymentFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToExpertDashboard() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExpertDashBoardFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToExpertProfilePage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExpertProfileFragment())
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
        }
    }

    private fun navigateToEditProfile() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, EditExpertProfileFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToRewardsPage() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExpertRewardsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToChangePassword() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ExpertChangePasswordFragment())
            .addToBackStack(null)
            .commit()
    }
}
