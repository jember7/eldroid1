package com.capstone.homeease.view

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentUserDashBoardBinding
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.model.LoginResponse
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserDashBoardFragment : Fragment(R.layout.fragment_user_dash_board) {

    private lateinit var binding: FragmentUserDashBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDashBoardBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(requireContext(), "Back action is disabled on the dashboard", Toast.LENGTH_SHORT).show()
            }
        })
        // Load user data such as address
        loadUserProfile()

        // Handle service selection click
        binding.washing.setOnClickListener { navigateToExperts("Car Washing") }
        binding.security.setOnClickListener { navigateToExperts("Home Security") }
        binding.homeservice.setOnClickListener { navigateToExperts("Home Service") }
        binding.laundry.setOnClickListener { navigateToExperts("Laundry") }
        binding.plumber.setOnClickListener { navigateToExperts("Plumbing") }
        binding.renting.setOnClickListener { navigateToExperts("Electrician") }

        binding.activity.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ActivityPageFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.textHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, UserDashBoardFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.payment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PaymentFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }


        return binding.root
    }

    private fun loadUserProfile() {
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

        api.getUserProfile(userId).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    binding.address.text = user?.address ?: "No address available"
                    Log.d("UserProfile", "Address: ${user?.address}")
                } else {
                    Log.e("UserDashBoardFragment", "Failed to load profile. Response code: ${response.code()}")
                    Log.e("UserDashBoardFragment", "Error message: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun navigateToExperts(profession: String) {
        val fragment = AvailableExpertsFragment.newInstance(profession)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
