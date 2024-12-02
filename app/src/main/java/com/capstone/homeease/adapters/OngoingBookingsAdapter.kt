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
import com.capstone.homeease.model.Booking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OngoingBookingsAdapter(var context: Context, var bookings: List<Booking>) : RecyclerView.Adapter<OngoingBookingsAdapter.BookingViewHolder>() {

    private var ongoingBookings: List<Booking> = bookings

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookings, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        // Log the booking data for debugging
        Log.d("OngoingBookings", "Binding booking at position: $position with name: ${booking.userName}")

        // Bind data to the views
        holder.expertNameTextView.text = booking.userName
        holder.address.text = "Address: ${booking.userAddress}"
        holder.bookingStatusTextView.text = "Status: ${booking.status}"
        holder.noteTextView.text = "Note: ${booking.note}"

        // Safe date parsing
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) // Correct format
            val date = dateFormat.parse(booking.timestamp) // Parse the timestamp string into Date
            if (date != null) {
                holder.bookingTimestampTextView.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)
            } else {
                holder.bookingTimestampTextView.text = "Invalid Date"
            }
        } catch (e: Exception) {
            Log.e("OngoingBookings", "Error parsing timestamp: ${booking.timestamp}", e)
            holder.bookingTimestampTextView.text = "Invalid Date"
        }

        // Set visibility and click listeners for the buttons
        when (booking.status.toLowerCase()) {  // Ensure status comparison is case insensitive
            "pending" -> {
                holder.acceptButton.visibility = View.VISIBLE
                holder.declineButton.visibility = View.VISIBLE
                holder.completeButton.visibility = View.GONE
                holder.cancelButton.visibility = View.GONE
            }
            "ongoing" -> {
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

        // Handle button actions
        holder.completeButton.setOnClickListener {
            // Update status to 'Completed' when the button is clicked
            updateBookingStatus(booking, "completed", holder)
        }
    }



    override fun getItemCount(): Int {
        return bookings.size
    }

    fun updateBookings(newBookings: List<Booking>) {
        this.bookings = newBookings
        notifyDataSetChanged()
    }

    private fun updateBookingStatus(booking: Booking, status: String, holder: BookingViewHolder) {
        // Simulate a network call (in reality, this would be an API call)
        val updatedBookings = bookings.toMutableList()
        val index = updatedBookings.indexOfFirst { it.id == booking.id }
        if (index != -1) {
            val updatedBooking = booking.copy(status = status)
            updatedBookings[index] = updatedBooking
            bookings = updatedBookings
            notifyItemChanged(index)

            Toast.makeText(context, "Booking $status", Toast.LENGTH_SHORT).show()
        }
    }

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expertNameTextView: TextView = itemView.findViewById(R.id.expertNameTextView)
        val address: TextView = itemView.findViewById(R.id.address)
        val bookingStatusTextView: TextView = itemView.findViewById(R.id.bookingStatusTextView)
        val bookingTimestampTextView: TextView = itemView.findViewById(R.id.bookingTimestampTextView)
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        val declineButton: Button = itemView.findViewById(R.id.declineButton)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
    }
}
