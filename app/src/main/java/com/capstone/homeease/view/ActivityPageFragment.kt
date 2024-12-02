package com.capstone.homeease.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.homeease.R
import com.capstone.homeease.adapters.BookingsAdapter
import com.capstone.homeease.model.ActivityViewModel
import com.capstone.homeease.databinding.FragmentActivityBinding
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId


class ActivityPageFragment : Fragment() {

    private lateinit var binding: FragmentActivityBinding
    private lateinit var pendingAdapter: BookingsAdapter
    private lateinit var ongoingAdapter: BookingsAdapter
    private lateinit var activityViewModel: ActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize ViewModel
        activityViewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)

        // Inflate the layout
        binding = FragmentActivityBinding.inflate(inflater, container, false)

        // Set up RecyclerViews and adapters
        binding.pendingRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.ongoingRecyclerView.layoutManager = LinearLayoutManager(context)

        pendingAdapter = BookingsAdapter(requireContext(), mutableListOf())
        ongoingAdapter = BookingsAdapter(requireContext(), mutableListOf())

        binding.pendingRecyclerView.adapter = pendingAdapter
        binding.ongoingRecyclerView.adapter = ongoingAdapter

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
                .replace(R.id.fragmentContainer, UserDashBoardFragment())
                .commitAllowingStateLoss()
        }
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .commit()
        }

        // Observe LiveData for ongoing and pending bookings
        activityViewModel.ongoingBookings.observe(viewLifecycleOwner) { bookings ->
            ongoingAdapter.updateBookings(bookings)
        }

        activityViewModel.pendingBookings.observe(viewLifecycleOwner) { bookings ->
            pendingAdapter.updateBookings(bookings)
        }

        activityViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            // Handle error (e.g., show a Toast or Snackbar)
            Log.e("ActivityPageFragment", "Error: $error")
        }

        binding.historyButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HistoryFragment())
                .commitAllowingStateLoss()
        }
        // Fetch the bookings using the ViewModel (replace with actual user ID)
        val userId = getUserId(requireContext()) // Assuming getUserId() returns an integer
        activityViewModel.fetchBookings(userId)

        return binding.root
    }
}

