package com.capstone.homeease.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.capstone.homeease.R

class SplashFragment : Fragment(R.layout.fragment_splash_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Delay the fragment replacement to avoid executing during FragmentManager transaction
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                showHomeFragment()
            }
        }, 2000)
    }

    private fun showHomeFragment() {
        val homeFragment = HomeFragment()

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, homeFragment)
        transaction.commit()
    }

}