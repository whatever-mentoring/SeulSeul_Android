package com.timi.seulseul.data.repository

import com.timi.seulseul.data.api.SubwayRouteService
import com.timi.seulseul.data.model.response.SubwayRouteData
import timber.log.Timber
import javax.inject.Inject

class SubwayRouteRepo @Inject constructor(
    private val subwayRouteService: SubwayRouteService
) {
    suspend fun getSubwayRoute() : Result<SubwayRouteData> {
        return try {
            val response = subwayRouteService.getSubwayRoute()

            if (response.isSuccessful) {
                Timber.d("Get SubwayRoute successful ${response.body()}")

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
            Timber.e("Exception when get SubwayRoute : $e")
            Result.failure(e)
        }
    }
}