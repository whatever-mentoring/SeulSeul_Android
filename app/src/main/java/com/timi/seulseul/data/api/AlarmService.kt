package com.timi.seulseul.data.api

import com.timi.seulseul.data.model.request.AlarmRequest
import com.timi.seulseul.data.model.response.AlarmResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AlarmService {
    @POST("v1/alarm")
    suspend fun postAlarm(@Body alarm : AlarmRequest) : Response<AlarmResponse>
}