package com.capstone.homeease.view

import com.capstone.homeease.model.Expert

interface ExpertView {
    fun showExperts(expertsList: List<Expert>)
    fun showError(errorMessage: String)
}
