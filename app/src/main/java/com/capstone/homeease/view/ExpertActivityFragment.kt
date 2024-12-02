package com.capstone.homeease.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.homeease.R
import com.capstone.homeease.adapters.ExpertBookingsAdapter
import com.capstone.homeease.databinding.FragmentExpertActivityBinding
import com.capstone.homeease.model.ApiResponse2
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExpertActivityFragment : Fragment() {

    private lateinit var binding: FragmentExpertActivityBinding
    private lateinit var ongoingRecyclerView: RecyclerView
    private lateinit var ebAdapter: ExpertBookingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentExpertActivityBinding.inflate(inflater, container, false)

        ongoingRecyclerView = binding.ongoingRecyclerView
        ongoingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ebAdapter = ExpertBookingsAdapter(requireContext(), mutableListOf())
        ongoingRecyclerView.adapter = ebAdapter
        // Fetch bookings data from the backend
        fetchBookings()
        binding.activity.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpertActivityFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.payment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpertPaymentFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.textHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpertDashBoardFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ExpertProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }

    private fun fetchBookings() {
        val expertId = getUserId(requireContext()) // Use the expert's user ID for the API call
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/api/") // Change base URL if needed
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(LaravelApi::class.java)

        api.getExpertBookings(expertId).enqueue(object : Callback<ApiResponse2> {
            override fun onResponse(call: Call<ApiResponse2>, response: Response<ApiResponse2>) {
                if (response.isSuccessful) {
                    val bookings = response.body()?.data
                    if (bookings != null && bookings.isNotEmpty()) {
                        Log.d("ExpertActivityFragment", "Bookings fetched successfully: ${bookings.size} bookings found.")
                        // Update the adapter with the fetched bookings
                        ebAdapter.updateBookings(bookings)

                    } else {
                        Log.d("ExpertActivityFragment", "No bookings available")
                    }
                } else {
                    Log.e("ExpertActivityFragment", "Failed to load expert bookings. Response: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to load expert bookings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse2>, t: Throwable) {
                Log.e("ExpertActivityFragment", "Error fetching bookings: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
