package com.capstone.homeease.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.homeease.R
import com.capstone.homeease.adapters.BookingsAdapter
import com.capstone.homeease.databinding.FragmentHistoryBinding
import com.capstone.homeease.model.ActivityViewModel
import com.capstone.homeease.model.Booking
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import com.capstone.homeease.view.ActivityPageFragment
import com.capstone.homeease.view.HomeFragment

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookingsAdapter: BookingsAdapter
    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        activityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch all bookings
        val userId = getUserId(requireContext()) // Fetch userId
        activityViewModel.fetchAllBookings(userId)

        // Observe all bookings LiveData
        activityViewModel.allBookings.observe(viewLifecycleOwner) { bookings ->
            bookings?.let {
                // Update the adapter with the fetched bookings
                bookingsAdapter.updateBookings(it)
            }
        }

        // Set up navigation
        setupNavigation()

        return binding.root
    }

    private fun setupRecyclerView() {
        bookingsAdapter = BookingsAdapter(requireContext(), mutableListOf())
        binding.ongoingRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingsAdapter
        }
    }

    private fun setupNavigation() {
        binding.activity.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ActivityPageFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.payment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PaymentFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.textHome.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, UserDashBoardFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.messages.setOnClickListener {

        }
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
