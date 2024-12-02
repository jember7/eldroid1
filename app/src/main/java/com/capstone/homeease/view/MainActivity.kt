package com.capstone.homeease.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.homeease.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Directly add the SplashFragment to the fragment container
            val splashFragment = SplashFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, splashFragment) // Use the correct container ID
                .commitNow() // Commit immediately
        }
    }
}


