package com.timi.seulseul.data.api

import com.timi.seulseul.data.model.request.AlarmPatchRequest
import com.timi.seulseul.data.model.request.AlarmPostRequest
import com.timi.seulseul.data.model.response.AlarmEnabledPatchResponse
import com.timi.seulseul.data.model.response.AlarmPatchResponse
import com.timi.seulseul.data.model.response.AlarmPostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AlarmService {
    @POST("v1/alarm")
    suspend fun postAlarm(@Body alarm : AlarmPostRequest) : Response<AlarmPostResponse>

    @PATCH("v1/alarm")
    suspend fun patchAlarm(@Body alarm : AlarmPatchRequest) : Response<AlarmPatchResponse>

    @PATCH("v1/alarm/enabled/{id}")
    suspend fun patchAlarmEnabled(@Path("id") id : Int) : Response<AlarmEnabledPatchResponse>
}