package com.timi.seulseul.data.repository

import android.util.Log
import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthData
import javax.inject.Inject

// UserRepository.kt
class AuthRepo @Inject constructor(
    private val apiService : ApiService
) {

    suspend fun postAuth(auth: Auth): Result<AuthData> {
        return try {
            val response = apiService.postAuth(auth)
            if (response.isSuccessful) {
                Log.d("jhb", "Post auth successful ${response.code()}")
                Result.success(response.body()?.data!!)
            } else {
                Log.e("jhb", "Error posting auth, code: ${response.code()}")
                Result.failure(RuntimeException("Error posting auth"))
            }
        } catch (e: Exception) {
            Log.e("jhb", "Exception when posting auth", e)
            Result.failure(e)
        }
    }
}