package com.timi.seulseul.data.repository

import android.util.Log
import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.response.GetEndLocation
import javax.inject.Inject

class GetEndLocationRepo @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun GetEndLocation(): Result<GetEndLocation> {
        return try {
            val response = apiService.getEndLocation()
            if (response.isSuccessful) {
                Log.d("smj", "get end location successful ${response.body()}")
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
            Log.e("smj", "Error in getEndLocation: ${e.message}", e)
            Result.failure(e)
        }
    }
}
