package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.AlarmService
import com.timi.seulseul.data.model.request.AlarmPatchRequest
import com.timi.seulseul.data.model.request.AlarmPostRequest
import com.timi.seulseul.data.model.response.AlarmEnabledData
import com.timi.seulseul.data.model.response.AlarmPatchData
import com.timi.seulseul.data.model.response.AlarmPostData
import timber.log.Timber
import javax.inject.Inject

class AlarmRepo @Inject constructor(
    private val alarmService: AlarmService
) {
    suspend fun postAlarm(alarm : AlarmPostRequest): Result<AlarmPostData> {
        return try {
            val response = alarmService.postAlarm(alarm)

            if (response.isSuccessful) {
                Timber.d("Post Alarm successful ${response.body()}")

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

    suspend fun patchAlarm(alarm : AlarmPatchRequest): Result<AlarmPatchData> {
        return try {
            val response = alarmService.patchAlarm(alarm)

            if (response.isSuccessful) {
                Timber.d("Patch Alarm successful ${response.body()}")

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
            Timber.e("Exception when patching Alarm : $e")
            Result.failure(e)
        }
    }

    suspend fun patchAlarmEnabled(id : Int) : Result<AlarmEnabledData> {
        return try {
            val response = alarmService.patchAlarmEnabled(id)

            if (response.isSuccessful) {
                Timber.d("Patch Alarm Enabled successful ${response.body()}")

                response.body()?.data?.let {
                    Result.success(it)
                } ?: run {
                    Result.failure(RuntimeException("Response body is null of data is missing"))
                }
            } else {
                val errorMsg = ErrorUtils.getErrorMessage(response.code())

                Timber.e(errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }
        } catch (e: Exception) {
            Timber.e("Exception when patching AlarmEnabled : $e")
            Result.failure(e)
        }
    }
}