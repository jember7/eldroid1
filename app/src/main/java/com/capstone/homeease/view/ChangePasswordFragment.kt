package com.capstone.homeease.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.model.ApiResponse
import com.capstone.homeease.model.ChangePasswordRequest
import com.capstone.homeease.network.ApiService
import com.capstone.homeease.network.RetrofitClient
import com.capstone.homeease.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var currentPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var changePasswordButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText)
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText)
        changePasswordButton = view.findViewById(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val currentPassword = currentPasswordEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(requireContext(), "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = SharedPreferencesHelper.getUserId(requireContext())  // Get user ID
        val apiService = RetrofitClient.createService(ApiService::class.java)

        // Prepare the ChangePasswordRequest object
        val changePasswordRequest = ChangePasswordRequest(
            current_password = currentPassword,
            new_password = newPassword,
            new_password_confirmation = confirmPassword
        )

        // Make the API call
        val call = apiService.changePassword(userId.toString(), changePasswordRequest)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    val profileFragment = ProfileFragment()
                    fragmentTransaction.replace(R.id.fragmentContainer, profileFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                } else {
                    Toast.makeText(requireContext(), "Failed to change password: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("ChangePasswordFragment", "Error changing password", t)
                Toast.makeText(requireContext(), "Error changing password", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
