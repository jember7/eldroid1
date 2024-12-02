package com.capstone.homeease.utils



import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull


fun String.toMediaType(): MediaType {
    return this.toMediaTypeOrNull() ?: throw IllegalArgumentException("Invalid MediaType string: $this")
}



