package com.capstone.homeease.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.capstone.homeease.R
import com.capstone.homeease.model.User
import com.capstone.homeease.controller.UserRegistrationController


class UserRegistrationFragment : Fragment(R.layout.fragment_user_registration), UserRegistrationView {

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var controller: UserRegistrationController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = UserRegistrationController(this, requireContext())

        val fullNameEditText = view.findViewById<EditText>(R.id.fullNameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val passwordConfirmEditText = view.findViewById<EditText>(R.id.passwordConfirmEditText)
        val addressEditText = view.findViewById<EditText>(R.id.addressEditText)
        val numberEditText = view.findViewById<EditText>(R.id.numberEditText)
        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val uploadImageButton = view.findViewById<Button>(R.id.uploadImageButton)
        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)

        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = passwordConfirmEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()
            val number = numberEditText.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || number.isEmpty()) {
                showToast("All fields must be filled")
            } else if (password != confirmPassword) {
                showToast("Passwords do not match")
            } else if (password.length < 6) {
                showToast("Password should be at least 6 characters")
            } else {
                val user = User(fullName, email, password,confirmPassword, address, number, imageUri)
                controller.registerUser(user)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            view?.findViewById<ImageView>(R.id.profileImageView)?.setImageURI(imageUri)
        } else {
            showToast("Error getting selected file")
        }
    }

    override fun onSuccess(message: String) {
        showToast(message)
        navigateToLoginFragment()
    }

    override fun onFailure(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun navigateToLoginFragment() {
        // Navigate to LoginFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, LoginFragment())  // Replace with your container view ID and LoginFragment
        transaction.addToBackStack(null)  // Optional: add to back stack if you want to allow navigating back
        transaction.commit()
    }
}
