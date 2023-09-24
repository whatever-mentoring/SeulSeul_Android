package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.FcmTokenService
import com.timi.seulseul.data.model.response.FcmTokenResponse
import com.timi.seulseul.data.model.resquest.FcmTokenRequest
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
                val errorMsg = when(response.code()) {
                    400 -> "Bad Request"
                    404 -> "Not Found"
                    409 -> "Conflict"
                    500 -> "Internal Server Error"
                    -8 -> "필수 입력값 형식 및 범위 오류"
                    -9 -> "필수 입력값 누락"
                    410 -> "USER_NOT_FOUND"
                    411 -> "BASEROUTE_NOT_FOUND"
                    412 -> "ALARM_NOT_FOUND"
                    // 설정 해놓은 오류중에 없으면
                    else -> "Unknown error occurred with code: ${response.code()}"
                }
                // 로그로 errorMsg 출력
                Timber.e(errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }

        } catch (e: Exception) {
            // 예외 로그
            Timber.e("Exception when posting auth : $e")
            Result.failure(e)
        }
    }
}