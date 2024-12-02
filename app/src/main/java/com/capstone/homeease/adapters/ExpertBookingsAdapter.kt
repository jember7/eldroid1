package com.capstone.homeease.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstone.homeease.R
import com.capstone.homeease.model.ApiResponse2
import com.capstone.homeease.model.Booking
import com.capstone.homeease.network.LaravelApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpertBookingsAdapter(private val context: Context, private var bookings: List<Booking>) : RecyclerView.Adapter<ExpertBookingsAdapter.BookingViewHolder>() {
    private var allBookings: MutableList<Booking> = mutableListOf()
    private lateinit var expertBookingsAdapter: ExpertBookingsAdapter
    private lateinit var ongoingBookingsAdapter: OngoingBookingsAdapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookings, parent, false)
        return BookingViewHolder(view)
    }


    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        // Log the booking data for debugging
        Log.d("ExpertBookings", "Binding booking at position: $position with name: ${booking.userName}")
        val dateString = booking.timestamp // Ensure this is a proper string format, e.g., "2024-12-01T15:30:00"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        try {
            val date = dateFormat.parse(dateString)

            val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val formattedDate = displayFormat.format(date)

            holder.bookingTimestampTextView.text = formattedDate
        } catch (e: Exception) {
            e.printStackTrace()
            holder.bookingTimestampTextView.text = "Invalid Date" // Handle invalid dates gracefully
        }

        holder.expertNameTextView.text = booking.userName
        holder.address.text = "Address: ${booking.userAddress}"
        holder.bookingStatusTextView.text = "Status: ${booking.status}"
        holder.note.text = "Note: ${booking.note}"
        holder.acceptButton.setOnClickListener {
            val bookingId = booking.id // Replace with your booking ID field
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(LaravelApi::class.java)
            api.acceptBooking(bookingId.toInt()).enqueue(object : Callback<ApiResponse2> {
                override fun onResponse(call: Call<ApiResponse2>, response: Response<ApiResponse2>) {
                    if (response.isSuccessful) {
                        val bookings = response.body()?.data
                        if (bookings != null) {
                            allBookings.clear()
                            allBookings.addAll(bookings) // Store all bookings
                            val ongoingBookings = allBookings.filter { it.status == "ongoing" }
                            val completedBookings = allBookings.filter { it.status == "pending" }

                            expertBookingsAdapter.updateBookings(completedBookings)
                            ongoingBookingsAdapter.updateBookings(ongoingBookings)
                        }
                    }
                }


                override fun onFailure(call: Call<ApiResponse2>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Show and hide buttons based on the booking status
        when (booking.status) {
            "pending" -> {
                holder.acceptButton.visibility = View.VISIBLE
                holder.declineButton.visibility = View.VISIBLE
                holder.completeButton.visibility = View.GONE
                holder.cancelButton.visibility = View.GONE
            }
            "accepted" -> {
                holder.acceptButton.visibility = View.GONE
                holder.declineButton.visibility = View.GONE
                holder.completeButton.visibility = View.VISIBLE
                holder.cancelButton.visibility = View.GONE
            }
            else -> {
                holder.acceptButton.visibility = View.GONE
                holder.declineButton.visibility = View.GONE
                holder.completeButton.visibility = View.GONE
                holder.cancelButton.visibility = View.GONE
            }
        }

        // Set button click listeners
        holder.acceptButton.setOnClickListener {
            updateBookingStatus(booking, "Accepted")
        }

        holder.declineButton.setOnClickListener {
            updateBookingStatus(booking, "Declined")
        }

        holder.completeButton.setOnClickListener {
            updateBookingStatus(booking, "Completed")
        }
    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    // Update bookings data
    fun updateBookings(newBookings: List<Booking>) {
        this.bookings = newBookings
        notifyDataSetChanged()
    }

    // Simulate status update
    private fun updateBookingStatus(booking: Booking, status: String) {
        // Log when expertId is null
        if (booking.expertId == null) {
            Log.d("Booking", "Booking ID: ${booking.id}, Expert ID: ${booking.expertId}")
            Log.e("ExpertBookingsAdapter", "Expert ID is missing for booking: ${booking.id}")
            Toast.makeText(context, "Expert ID is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Proceed with the status update if expertId is not null
        val updatedBookings = bookings.toMutableList()
        val index = updatedBookings.indexOfFirst { it.id == booking.id }
        if (index != -1) {
            val updatedBooking = booking.copy(
                status = status,
                expertId = booking.expertId!! // Safe to access after null check
            )
            updatedBookings[index] = updatedBooking
            bookings = updatedBookings
            notifyItemChanged(index)
            Toast.makeText(context, "Booking $status", Toast.LENGTH_SHORT).show()
        }
    }


    // ViewHolder class for binding item views
    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expertNameTextView: TextView = itemView.findViewById(R.id.expertNameTextView)
        val address: TextView = itemView.findViewById(R.id.address)
        val bookingStatusTextView: TextView = itemView.findViewById(R.id.bookingStatusTextView)
        val bookingTimestampTextView: TextView = itemView.findViewById(R.id.bookingTimestampTextView)
        val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        val note: TextView = itemView.findViewById(R.id.noteTextView)
        val declineButton: Button = itemView.findViewById(R.id.declineButton)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
    }
}
