package com.capstone.homeease.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.homeease.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var loginButton: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views using findViewById
        imageView1 = rootView.findViewById(R.id.imageView1)
        imageView2 = rootView.findViewById(R.id.imageView2)
        imageView3 = rootView.findViewById(R.id.imageView3)
        loginButton = rootView.findViewById(R.id.login)

        // Set images for the UI
        imageView1.setImageResource(R.drawable.img)
        imageView2.setImageResource(R.drawable.img_35)
        imageView3.setImageResource(R.drawable.img_36)

        // Set click listeners to navigate to registration
        imageView2.setOnClickListener {
            navigateToRegistration(isUser = true)
        }

        imageView3.setOnClickListener {
            navigateToRegistration(isUser = false)
        }

        // Set click listener for login
        loginButton.setOnClickListener {
            navigateToLoginPage()
        }

        return rootView
    }

    // Handle registration navigation (User or Expert)
    private fun navigateToRegistration(isUser: Boolean) {
        val registrationFragment = if (isUser) {
            UserRegistrationFragment() // Fragment for user registration
        } else {
            ExpertRegistrationFragment() // Fragment for expert registration
        }

        // Perform the fragment transaction
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, registrationFragment)  // Replace with selected registration fragment
            .addToBackStack(null)  // Add to back stack for proper navigation
            .commit()
    }

    // Handle navigation to login page
    private fun navigateToLoginPage() {
        val loginFragment = LoginFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, loginFragment) // Replace with login fragment
            .addToBackStack(null)  // Add to back stack to allow back navigation
            .commit()
    }

}
