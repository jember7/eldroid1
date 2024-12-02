package com.capstone.homeease.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentPaymentBinding // Import the ViewBinding class

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null // Declare the ViewBinding variable
    private val binding get() = _binding!! // Get the binding object

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the ViewBinding and return the root view
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for buttons using the ViewBinding
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
        _binding = null // Clean up the binding when the fragment's view is destroyed
    }
}