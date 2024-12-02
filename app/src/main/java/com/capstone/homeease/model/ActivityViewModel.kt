package com.capstone.homeease.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {

    private val repository = BookingRepository()

    private val _allBookings = MutableLiveData<List<Booking>>()
    val allBookings: LiveData<List<Booking>> get() = _allBookings

    private val _ongoingBookings = MutableLiveData<List<Booking>>()
    val ongoingBookings: LiveData<List<Booking>> get() = _ongoingBookings

    private val _pendingBookings = MutableLiveData<List<Booking>>()
    val pendingBookings: LiveData<List<Booking>> get() = _pendingBookings

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Fetch all bookings for the user (for HistoryFragment)
    fun fetchAllBookings(userId: Int) {
        Log.d("ActivityViewModel", "Fetching all bookings for userId: $userId")
        repository.getBookings(userId) { bookings, error ->
            if (error != null) {
                _errorMessage.postValue(error ?: "Unknown error occurred")
                return@getBookings
            }

            // Post all bookings to LiveData
            _allBookings.postValue(bookings)
        }
    }

    // Fetch ongoing and pending bookings (for other pages)
    fun fetchBookings(userId: Int) {
        Log.d("ActivityViewModel", "Fetching bookings for userId: $userId")
        repository.getBookings(userId) { bookings, error ->
            if (error != null) {
                _errorMessage.postValue(error ?: "Unknown error occurred")
                return@getBookings
            }

            // Separate ongoing and pending bookings
            val ongoing = bookings?.filter { it.status == "ongoing" } ?: emptyList()
            val pending = bookings?.filter { it.status == "pending" } ?: emptyList()

            // Post the filtered lists to LiveData
            _ongoingBookings.postValue(ongoing)
            _pendingBookings.postValue(pending)
        }
    }
}

