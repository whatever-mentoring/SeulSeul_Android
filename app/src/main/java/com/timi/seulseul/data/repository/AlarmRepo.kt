package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.AlarmService
import com.timi.seulseul.data.model.request.AlarmRequest
import com.timi.seulseul.data.model.response.AlarmData
import timber.log.Timber
import javax.inject.Inject

class AlarmRepo @Inject constructor(
    private val alarmService: AlarmService
) {
    suspend fun postAlarm(alarm : AlarmRequest): Result<AlarmData> {
        return try {
            val response = alarmService.postAlarm(alarm)

            if (response.isSuccessful) {
                Timber.d("Post auth successful ${response.body()}")

                response.body()?.data?.let {
                    Result.success(it)
                } ?: run {
                    Result.failure(RuntimeException("Response body is null or data is missing"))
                }
            } else {
                val errorMsg = ErrorUtils.getErrorMessage(response.code())

                Timber.e(errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e("Exception when posting Alarm : $e")
            Result.failure(e)
        }
    }
}