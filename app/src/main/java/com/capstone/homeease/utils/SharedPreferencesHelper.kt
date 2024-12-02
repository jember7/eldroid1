package com.capstone.homeease.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import com.capstone.homeease.model.Expert
import com.capstone.homeease.model.ExpertProfileResponse

object SharedPreferencesHelper {
    private const val USER_ID_KEY = "USER_ID"
    private const val PREF_NAME = "user_prefs"
    private const val USER_NAME_KEY = "user_name"

    fun saveUserId(context: Context, userId: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }

    // Get the user ID from SharedPreferences
    fun getUserId(context: Context): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1) // Returns -1 if no user ID is found
    }


    fun saveUserIdToSession(context: Context, userId: Int) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(USER_ID_KEY, userId)
        editor.apply()
    }

    fun getUserIdFromSession(context: Context): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val userId = sharedPreferences.getInt(USER_ID_KEY, -1)
        Log.d("SharedPreferencesHelper", "Retrieved User ID: $userId")
        return userId
    }
    fun getUserNameFromSession(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(USER_NAME_KEY, "") ?: ""
    }

    fun saveUserNameToSession(context: Context, userName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(USER_NAME_KEY, userName)
        editor.apply() // Save asynchronously
    }



}
