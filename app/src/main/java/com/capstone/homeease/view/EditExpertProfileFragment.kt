package com.capstone.homeease.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.databinding.FragmentEditExpertProfileBinding
import com.capstone.homeease.model.ExpertProfileResponse
import com.capstone.homeease.network.ApiService
import com.capstone.homeease.network.RetrofitClient
import com.capstone.homeease.utils.SharedPreferencesHelper.getUserId
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditExpertProfileFragment : Fragment(R.layout.fragment_edit_expert_profile) {

    private lateinit var binding: FragmentEditExpertProfileBinding
    private lateinit var selectedProfession: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditExpertProfileBinding.bind(view)

        // Set up profession spinner
        val professions = listOf("Car Washing", "Home Security", "Laundry", "Plumbing", "Electrician", "Home Service")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, professions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.professionSpinner.adapter = adapter
        binding.professionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedProfession = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Set up save button
        binding.saveButton.setOnClickListener {
            saveExpertProfile()
        }

        // Load expert profile
        loadExpertProfile()
    }

    private fun loadExpertProfile() {
        val userId = getUserId(requireContext())
        val apiService = RetrofitClient.createService(ApiService::class.java)
        val call = apiService.getExpertProfile(userId)

        call.enqueue(object : Callback<ExpertProfileResponse> {
            override fun onResponse(call: Call<ExpertProfileResponse>, response: Response<ExpertProfileResponse>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    profile?.let {
                        binding.fullNameEditText.setText(it.fullName)
                        binding.emailEditText.setText(it.email)
                        binding.addressEditText.setText(it.address)
                        binding.numberEditText.setText(it.phoneNumber)
                        binding.dateOfBirthEditText.setText(it.dateOfBirth)
                    }
                } else {
                    Log.e("EditExpertProfile", "Failed to load profile")
                }
            }

            override fun onFailure(call: Call<ExpertProfileResponse>, t: Throwable) {
                Log.e("EditExpertProfile", "Error fetching profile", t)
            }
        })
    }

    private fun saveExpertProfile() {
        val fullName = binding.fullNameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val address = binding.addressEditText.text.toString().trim()
        val number = binding.numberEditText.text.toString().trim()
        val dateOfBirth = binding.dateOfBirthEditText.text.toString().trim()

        if (dateOfBirth.isEmpty()) {
            Toast.makeText(requireContext(), "Date of Birth is required", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = getUserId(requireContext())

        val apiService = RetrofitClient.createService(ApiService::class.java)
        val call = apiService.updateExpertProfile(
            userId.toString(), fullName, email, address, number, selectedProfession, dateOfBirth
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragmentContainer, ExpertProfileFragment())
                    fragmentTransaction.commit()
                } else {
                    // Log the response body to understand the error
                    Log.e("EditExpertProfile", "Failed to update profile: ${response.message()}")
                    Toast.makeText(requireContext(), "Failed to update profile: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("EditExpertProfile", "Error updating profile", t)
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
