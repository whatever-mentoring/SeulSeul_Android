package com.timi.seulseul.data.repository

import android.util.Log
import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.Location
import com.timi.seulseul.data.model.LocationData
import javax.inject.Inject

class LocationRepo @Inject constructor(
    private val apiService : ApiService
) {


    // location: Location 객체를 받아서, 서버에 Request하고, Result<T> 객체를 Response한다(LocationData).
    suspend fun postLocation(location: Location): Result<LocationData> {
        return try {
            // 서버에 post request
            val response = apiService.postLocation(location)
            // response 성공 여부 확인
            if (response.isSuccessful) {
                // 성공 로그 -> response.body()데이터 전체 추출
                Log.d("jhb", "Post location successful ${response.body()}")
                // response.body()에서 data만 추출
                response.body()?.data?.let {
                    // data 있으면 response.body().data 추출
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
                Log.e("jhb", errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }

        } catch (e: Exception) {
            // 예외 로그
            Log.e("jhb", "Exception when posting location", e)
            Result.failure(e)
        }
    }
}