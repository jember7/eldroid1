package com.capstone.homeease.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.homeease.R
import com.capstone.homeease.adapters.ExpertsAdapter
import com.capstone.homeease.controller.ExpertController
import com.capstone.homeease.model.Expert

class AvailableExpertsFragment : Fragment(), ExpertView {

    private lateinit var expertsRecyclerView: RecyclerView
    private lateinit var expertsAdapter: ExpertsAdapter
    private var experts: MutableList<Expert> = mutableListOf()
    private lateinit var expertController: ExpertController
    private val expertIdMap = mutableMapOf<String, Int>()

    companion object {
        private const val ARG_PROFESSION = "profession"

        fun newInstance(profession: String): AvailableExpertsFragment {
            val fragment = AvailableExpertsFragment()
            val args = Bundle()
            args.putString(ARG_PROFESSION, profession) // Use the correct key here
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_available_experts, container, false)

        expertsRecyclerView = binding.findViewById(R.id.expertsRecyclerView)
        expertsRecyclerView.layoutManager = LinearLayoutManager(context)
        expertsAdapter = ExpertsAdapter(requireContext(), experts, expertIdMap)
        expertsRecyclerView.adapter = expertsAdapter

        expertController = ExpertController(this)

        val profession = arguments?.getString(ARG_PROFESSION) ?: ""
        val professionTitle = binding.findViewById<TextView>(R.id.title)
        professionTitle.text = profession

        expertController.fetchExperts(profession)
        // Add fragment transactions for the buttons
        binding.findViewById<View>(R.id.activity).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ActivityPageFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.findViewById<View>(R.id.textHome).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, UserDashBoardFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
        binding.findViewById<View>(R.id.payment).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PaymentFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.findViewById<View>(R.id.profile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        return binding
    }

    override fun showExperts(expertsList: List<Expert>) {
        expertsAdapter.updateExperts(expertsList)

        // Immediately fetch expert ID for each expert
        expertsList.forEach { expert ->
            val expertEmail = expert.email // Assuming 'email' is a property of Expert

            // Fetch the expert ID by email
            expertController.getExpertIdByEmail(expertEmail) { expertId ->
                if (expertId != null) {
                    // You can store the expert ID, use it immediately or show a toast
                    Toast.makeText(context, "Expert ID for ${expert.fullName}: $expertId", Toast.LENGTH_SHORT).show()
                    expertIdMap[expertEmail] = expertId
                } else {
                    Toast.makeText(context, "Expert not found: ${expert.fullName}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun showError(errorMessage: String) {
        // Handle error, show toast or UI feedback
    }
}