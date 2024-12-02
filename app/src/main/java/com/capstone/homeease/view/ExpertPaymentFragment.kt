package com.capstone.homeease.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentExpertPaymentBinding
import com.capstone.homeease.databinding.FragmentPaymentBinding


class ExpertPaymentFragment : Fragment() {
    private var _binding: FragmentExpertPaymentBinding? = null // Declare the ViewBinding variable
    private val binding get() = _binding!! // Get the binding object

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the ViewBinding and return the root view
        _binding = FragmentExpertPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listeners for buttons using the ViewBinding
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding when the fragment's view is destroyed
    }
}