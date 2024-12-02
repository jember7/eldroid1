package com.capstone.homeease.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstone.homeease.model.Booking
import com.capstone.homeease.databinding.ItemBookingsBinding
import com.capstone.homeease.model.ApiResponse
import com.capstone.homeease.network.LaravelApi
import com.capstone.homeease.network.RetrofitClient
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Locale

class BookingsAdapter(
    private val context: Context,
    private var bookings: MutableList<Booking> // Make the list mutable
) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        // Inflate the layout using ViewBinding
        val binding = ItemBookingsBinding.inflate(LayoutInflater.from(context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.bind(booking)
    }

    override fun getItemCount(): Int {
        return bookings.size
    }


    fun updateBookings(newBookings: List<Booking>) {
        bookings.clear()
        bookings.addAll(newBookings)
        notifyDataSetChanged()
    }


    inner class BookingViewHolder(private val binding: ItemBookingsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            Log.d("BookingsAdapter", "Expert Name: ${booking.expertName}")
            binding.expertNameTextView.text = "Expert: ${booking.expertName}"
            binding.address.text = "Address: ${booking.expertAddress}"
            binding.bookingStatusTextView.text = "Status: ${booking.status}"

            val timestamp: String = booking.timestamp
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = formatter.parse(timestamp) // Parse the timestamp string to Date
                val formattedTimestamp = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)
                binding.bookingTimestampTextView.text = formattedTimestamp
            } catch (e: Exception) {
                binding.bookingTimestampTextView.text = "Invalid Timestamp"
            }


            binding.bookingTimestampTextView.text = timestamp
            binding.noteTextView.text = booking.note


            binding.cancelButton.setOnClickListener {
                val apiService = RetrofitClient.createService(LaravelApi::class.java)

                // Call the API to update booking status
                apiService.updateBookingStatus(booking.id.toInt()).enqueue(object : retrofit2.Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: retrofit2.Response<ApiResponse>) {
                        if (response.isSuccessful) {
                            val apiResponse = response.body()
                            if (apiResponse?.success == true) {
                                // Booking status updated successfully
                                Toast.makeText(context, apiResponse.message ?: "Booking cancelled successfully", Toast.LENGTH_SHORT).show()

                                // Remove the booking from the list and update RecyclerView
                                bookings.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                            } else {
                                // Display the error message if success is false
                                Toast.makeText(context, "Failed: ${apiResponse?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // API response is not successful
                            Toast.makeText(context, "Failed to update booking status: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        // Handle network error
                        Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }



            // Button visibility based on status
            when (booking.status) {
                "pending" -> {
                    binding.acceptButton.visibility = View.GONE
                    binding.declineButton.visibility = View.GONE
                    binding.cancelButton.visibility = View.VISIBLE
                    binding.completeButton.visibility = View.GONE
                }
                "ongoing" -> {
                    binding.acceptButton.visibility = View.GONE
                    binding.declineButton.visibility = View.GONE
                    binding.cancelButton.visibility = View.VISIBLE
                    binding.completeButton.visibility = View.GONE
                }
                "completed" -> {
                    binding.acceptButton.visibility = View.GONE
                    binding.declineButton.visibility = View.GONE
                    binding.cancelButton.visibility = View.GONE
                    binding.completeButton.visibility = View.GONE
                }
                else -> {
                    binding.acceptButton.visibility = View.GONE
                    binding.declineButton.visibility = View.GONE
                    binding.cancelButton.visibility = View.GONE
                    binding.completeButton.visibility = View.GONE
                }
            }
        }
    }
}
