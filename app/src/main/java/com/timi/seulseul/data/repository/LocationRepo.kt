package com.timi.seulseul.data.repository

import android.util.Log
import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.model.Location
import com.timi.seulseul.data.model.LocationData
import com.timi.seulseul.data.model.PatchLocation
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
                // http 응답 상태 코드에 따라 분류 -> ErrorUtils로 관리
                val errorMsg = ErrorUtils.getErrorMessage(response.code())

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


    suspend fun patchLocation(patchLocation: PatchLocation): Result<LocationData> {
        return try {
            // 서버에 post request
            val response = apiService.patchLocation(patchLocation)
            // response 성공 여부 확인
            if (response.isSuccessful) {
                // 성공 로그 -> response.body()데이터 전체 추출
                Log.d("jhb", "PATCH location successful ${response.body()}")
                // response.body()에서 data만 추출
                response.body()?.data?.let {
                    // data 있으면 response.body().data 추출
                    Result.success(it)
                } ?: run {
                    // data가 null인 경우 에러
                    Result.failure(RuntimeException("Response body is null or data is missing"))
                }

            } else {
                // http 응답 상태 코드에 따라 분류 -> ErrorUtils로 관리
                val errorMsg = ErrorUtils.getErrorMessage(response.code())

                // 로그로 errorMsg 출력
                Log.e("jhb", errorMsg)
                Result.failure(RuntimeException(errorMsg))
            }

        } catch (e: Exception) {
            // 예외 로그
            Log.e("jhb", "Exception when patching location", e)
            Result.failure(e)
        }
    }
}