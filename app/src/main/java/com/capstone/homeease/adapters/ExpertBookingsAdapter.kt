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
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookings, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

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

        holder.expertNameTextView.text = booking.userName
        holder.address.text = "Address: ${booking.userAddress}"
        holder.bookingStatusTextView.text = "Status: ${booking.status}"
        holder.note.text = "Note: ${booking.note}"

        holder.acceptButton.setOnClickListener {
            if (booking.expertId == null) {
                Log.e("ExpertBookingsAdapter", "Expert ID is missing for booking: ${booking.id}")
                Toast.makeText(context, "Expert ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookingId = booking.id
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/") // Use your actual API base URL here
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(LaravelApi::class.java)
            api.acceptBooking(bookingId.toInt()).enqueue(object : Callback<ApiResponse2> {
                override fun onResponse(call: Call<ApiResponse2>, response: Response<ApiResponse2>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("APIResponse", "Response Body: ${Gson().toJson(responseBody)}")  // Log the full response

                        val gson = Gson()


                        if (responseBody != null) {
                            val jsonElement = gson.toJsonTree(responseBody.data)
                            if (jsonElement.isJsonArray) {
                                // Handle response if 'data' is an array
                                val updatedBookingsList: List<Booking> = gson.fromJson(jsonElement, Array<Booking>::class.java).toList()
                                bookings = updatedBookingsList
                                notifyDataSetChanged()
                            } else if (jsonElement.isJsonObject) {
                                // Handle response if 'data' is a single object
                                val updatedBooking: Booking = gson.fromJson(jsonElement, Booking::class.java)
                                val updatedBookings = bookings.toMutableList()
                                val index = updatedBookings.indexOfFirst { it.id == updatedBooking.id }
                                if (index != -1) {
                                    updatedBookings[index] = updatedBooking
                                    bookings = updatedBookings
                                    notifyItemChanged(holder.adapterPosition)
                                }
                            }
                        }

                    } else {
                        Log.e("ExpertBookingsAdapter", "Response failed with code: ${response.code()}")
                        Toast.makeText(context, "Failed to accept booking: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }




                override fun onFailure(call: Call<ApiResponse2>, t: Throwable) {
                    // Log the failure details
                    Log.e("ExpertBookingsAdapter", "Error accepting booking: ${booking.id}, Error: ${t.message}")
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        when (booking.status) {
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
        holder.declineButton.setOnClickListener {
            if (booking.expertId == null) {
                Log.e("ExpertBookingsAdapter", "Expert ID is missing for booking: ${booking.id}")
                Toast.makeText(context, "Expert ID is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookingId = booking.id
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/") // Use your actual API base URL here
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(LaravelApi::class.java)
            api.declineBooking(bookingId.toInt()).enqueue(object : Callback<ApiResponse2> {
                override fun onResponse(call: Call<ApiResponse2>, response: Response<ApiResponse2>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("APIResponse", "Response Body: ${Gson().toJson(responseBody)}")  // Log the full response

                        val gson = Gson()

                        if (responseBody != null) {
                            val jsonElement = gson.toJsonTree(responseBody.data)
                            if (jsonElement.isJsonArray) {
                                // Handle response if 'data' is an array
                                val updatedBookingsList: List<Booking> = gson.fromJson(jsonElement, Array<Booking>::class.java).toList()
                                bookings = updatedBookingsList
                                notifyDataSetChanged()
                            } else if (jsonElement.isJsonObject) {
                                // Handle response if 'data' is a single object
                                val updatedBooking: Booking = gson.fromJson(jsonElement, Booking::class.java)
                                val updatedBookings = bookings.toMutableList()
                                val index = updatedBookings.indexOfFirst { it.id == updatedBooking.id }
                                if (index != -1) {
                                    updatedBookings[index] = updatedBooking
                                    bookings = updatedBookings
                                    notifyItemChanged(holder.adapterPosition)
                                }
                            }
                        }

                    } else {
                        Log.e("ExpertBookingsAdapter", "Response failed with code: ${response.code()}")
                        Toast.makeText(context, "Failed to decline booking: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse2>, t: Throwable) {
                    // Log the failure details
                    Log.e("ExpertBookingsAdapter", "Error declining booking: ${booking.id}, Error: ${t.message}")
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    override fun getItemCount(): Int {
        return bookings.size
    }

    fun updateBookings(newBookings: List<Booking>) {
        this.bookings = newBookings
        notifyDataSetChanged()
    }

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

