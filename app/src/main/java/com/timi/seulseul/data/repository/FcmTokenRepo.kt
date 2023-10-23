package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.FcmTokenService
import com.timi.seulseul.data.model.response.FcmTokenResponse
import com.timi.seulseul.data.model.request.FcmTokenRequest
import timber.log.Timber
import javax.inject.Inject

class FcmTokenRepo @Inject constructor(
    private val fcmTokenService : FcmTokenService
) {

    suspend fun postFcmToken(fcmToken: FcmTokenRequest): Result<FcmTokenResponse> {
        return try {
            val response = fcmTokenService.postFcmToken(fcmToken)
            if (response.isSuccessful) {
                // 성공 로그 -> response.body()데이터 전체 추출
                Timber.d("postFcmToken : ${response.body()}")

                response.body()?.let {
                    Result.success(it)
                } ?: run {
                    // data가 null인 경우 에러
                    Result.failure(RuntimeException("Response body is null or data is missing"))
                }

            } else {
                // http 응답 상태 코드에 따라 분류
                val errorMsg = ErrorUtils.getErrorMessage(response.code())

                Timber.e(errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }

        } catch (e: Exception) {
            // 예외 로그
            Timber.e("Exception when posting FcmToken : $e")
            Result.failure(e)
        }
    }
}