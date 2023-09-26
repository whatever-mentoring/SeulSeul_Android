package com.timi.seulseul.data.repository

import android.util.Log
import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.response.EndLocationData
import javax.inject.Inject

class SetEndLocationRepo @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun setEndLocation(id : Int): Result<EndLocationData> {
        return try {
            val response = apiService.setEndLocation(id)
            if (response.isSuccessful) {
                Log.d("smj", "set end location successful ${response.body()}")
                response.body()?.let {
                    Result.success(it)
                } ?: run {
                    Result.failure(RuntimeException("Response body is null"))
                }
            } else {
                val errorMsg = "Error occurred: ${response.code()}"
                Log.e("smj", errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("smj", "Error in setEndLocation: ${e.message}", e)
            Result.failure(e)
        }
    }
}