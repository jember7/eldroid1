package com.capstone.homeease.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.homeease.R
import com.capstone.homeease.adapters.ExpertBookingsAdapter
import com.capstone.homeease.adapters.OngoingBookingsAdapter
import com.capstone.homeease.databinding.FragmentExpertDashBoardBinding
import com.capstone.homeease.model.ApiResponse2
import com.capstone.homeease.model.Booking
import com.capstone.homeease.model.ExpertProfileResponse
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExpertDashBoardFragment : Fragment(R.layout.fragment_expert_dash_board) {

    private lateinit var binding: FragmentExpertDashBoardBinding
    private lateinit var expertBookingsAdapter: ExpertBookingsAdapter
    private lateinit var ongoingBookingsAdapter: OngoingBookingsAdapter

    private var allBookings: List<Booking> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpertDashBoardBinding.inflate(inflater, container, false)

        // Initialize RecyclerViews
        binding.bookingsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) // Vertical layout
        binding.ongoingBookingsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) // Vertical layout

        // Initialize both adapters with empty lists initially
        expertBookingsAdapter = ExpertBookingsAdapter(requireContext(), emptyList())
        ongoingBookingsAdapter = OngoingBookingsAdapter(requireContext(), emptyList())

        // Set the adapters for the RecyclerViews
        binding.bookingsRecyclerView.adapter = expertBookingsAdapter
        binding.ongoingBookingsRecyclerView.adapter = ongoingBookingsAdapter

        // Set click listeners for ImageViews (Navigation)
        setNavigationListeners()

        // Load expert profile and bookings
        loadExpertProfile()
        fetchExpertBookings() // Fetch all expert bookings and categorize

        return binding.root
    }

    private fun fetchExpertBookings() {
        val expertId = getUserId(requireContext()) // Use the expert's user ID for the API call
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") // Change base URL if needed
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)

        Log.d("ExpertDashBoardFragment", "Fetching bookings for expert ID: $expertId")

        api.getExpertBookings(expertId).enqueue(object : Callback<ApiResponse2> {
            override fun onResponse(call: Call<ApiResponse2>, response: Response<ApiResponse2>) {
                if (response.isSuccessful) {
                    val bookings = response.body()?.data
                    if (bookings != null && bookings.isNotEmpty()) {
                        Log.d("ExpertDashBoardFragment", "Bookings fetched successfully: ${bookings.size} bookings found.")
                        allBookings = bookings

                        // Categorize the bookings
                        val ongoingBookings = allBookings.filter { it.status == "ongoing" }
                        val completedBookings = allBookings.filter { it.status == "pending" }

                        Log.d("ExpertDashBoardFragment", "Ongoing Bookings: ${ongoingBookings.size}")
                        Log.d("ExpertDashBoardFragment", "Completed Bookings: ${completedBookings.size}")

                        ongoingBookingsAdapter.updateBookings(ongoingBookings)
                        expertBookingsAdapter.updateBookings(completedBookings)

                        // Ensure RecyclerView is updated
                        binding.bookingsRecyclerView.scrollToPosition(0)
                        binding.ongoingBookingsRecyclerView.scrollToPosition(0)

                    } else {
                        Log.d("ExpertDashBoardFragment", "No bookings available")
                    }
                } else {
                    Log.e("ExpertDashBoardFragment", "Failed to load expert bookings. Response: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to load expert bookings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse2>, t: Throwable) {
                Log.e("ExpertDashBoardFragment", "Error fetching bookings: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setNavigationListeners() {
        binding.activity.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ActivityPageFragment())
                .commitAllowingStateLoss()
        }
        binding.payment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PaymentFragment())
                .commit()
        }
        binding.textHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpertDashBoardFragment())
                .commitAllowingStateLoss()
        }
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .commit()
        }
    }

    private fun loadExpertProfile() {
        val expertId = getUserId(requireContext())
        if (expertId == -1) {
            Log.e("ExpertDashBoardFragment", "Expert ID not found")
            return
        }

        // Make Retrofit call to fetch the expert profile
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") // Change base URL if needed
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)

        api.getExpertProfile(expertId).enqueue(object : Callback<ExpertProfileResponse> {
            override fun onResponse(
                call: Call<ExpertProfileResponse>,
                response: Response<ExpertProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val expert = response.body()
                    Log.d("ExpertDashBoardFragment", "Expert Profile: $expert")
                    Log.d("ExpertDashBoardFragment", "Full name: ${expert?.fullName}, Phone number: ${expert?.phoneNumber}")

                    // Update the UI with the expert's data
                    binding.usernameText.text = expert?.fullName ?: "Expert Name"
                    binding.numberText.text = expert?.phoneNumber ?: "Add Phone Number"


                } else {
                    Log.e("ExpertDashBoardFragment", "Failed to load expert profile. Response: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ExpertProfileResponse>, t: Throwable) {
                Log.e("ExpertDashBoardFragment", "Error fetching expert profile: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
