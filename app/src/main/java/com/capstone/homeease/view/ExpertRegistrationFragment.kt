package com.capstone.homeease.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.controller.ExpertRegistrationController
import com.capstone.homeease.model.Expert
import java.text.SimpleDateFormat
import java.util.*

class ExpertRegistrationFragment : Fragment(R.layout.fragment_expert_registration), ExpertRegistrationView {

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var controller: ExpertRegistrationController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = ExpertRegistrationController(this, requireContext()) // Pass this as ExpertRegistrationView

        val fullNameEditText = view.findViewById<EditText>(R.id.fullNameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val passwordConfirmEditText = view.findViewById<EditText>(R.id.passwordConfirmEditText)
        val professionSpinner = view.findViewById<Spinner>(R.id.professionSpinner)
        val dateOfBirthEditText = view.findViewById<EditText>(R.id.dateOfBirthEditText)
        val numberEditText = view.findViewById<EditText>(R.id.numberEditText)
        val registerButton = view.findViewById<Button>(R.id.submitButton)
        val uploadImageButton = view.findViewById<Button>(R.id.uploadImageButton)
        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val addressEditText = view.findViewById<EditText>(R.id.addressEditText)

        // Handle the image upload
        uploadImageButton?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Handle the profile image click
        profileImageView?.setOnClickListener {
            // Example: Open the image picker or a dialog to allow the user to select an image
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Set the date picker for date of birth field
        dateOfBirthEditText?.setOnClickListener {
            showDatePickerDialog(dateOfBirthEditText)
        }

        // Set up the profession spinner with an ArrayAdapter
        val professions = listOf("Car Washing", "Home Security", "Laundry", "Plumbing", "Electrician", "Home Service")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, professions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        professionSpinner.adapter = adapter

        // Handle the registration form submission
        registerButton?.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = passwordConfirmEditText.text.toString().trim()
            val profession = professionSpinner.selectedItem.toString()
            val dateOfBirth = dateOfBirthEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val number = numberEditText.text.toString().trim()

            // Validate the input fields
            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || dateOfBirth.isEmpty() || profession.isEmpty() || number.isEmpty()) {
                showToast("All fields must be filled")
            } else if (password != confirmPassword) {
                showToast("Passwords do not match")
            } else {
                val expert = Expert(
                    fullName, email, password, confirmPassword,profession, dateOfBirth, address, number, imageUri
                )

                // Call the controller to handle the expert registration with image
                controller.registerExpert(expert) // Call the revised registerExpert method
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            // Display the selected image in the profileImageView
            view?.findViewById<ImageView>(R.id.profileImageView)?.setImageURI(imageUri)
        } else {
            showToast("Error getting selected file")
        }
    }

    private fun showDatePickerDialog(dateOfBirthEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                // Format the selected date as yyyy-MM-dd
                val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${(dayOfMonth).toString().padStart(2, '0')}"
                dateOfBirthEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(message: String) {
        showToast(message)
        navigateToLoginFragment()
    }

    override fun onFailure(message: String) {
        showToast(message)
    }
    private fun navigateToLoginFragment() {
        // Navigate to LoginFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, LoginFragment())  // Replace with your container view ID and LoginFragment
        transaction.addToBackStack(null)  // Optional: add to back stack if you want to allow navigating back
        transaction.commit()
    }
}
