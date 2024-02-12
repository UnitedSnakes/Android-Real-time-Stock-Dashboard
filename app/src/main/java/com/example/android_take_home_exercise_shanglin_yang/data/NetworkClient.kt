package com.example.android_take_home_exercise_shanglin_yang.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class NetworkClient {
    private val client: OkHttpClient = OkHttpClient()

    suspend fun performRequest(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()

        val maxRetries = 5

        repeat(maxRetries) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    if (!jsonString.isNullOrBlank()) {
                        return@withContext jsonString
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            kotlinx.coroutines.delay(1000) // retry after 1s
        }

        return@withContext ""
    }
}