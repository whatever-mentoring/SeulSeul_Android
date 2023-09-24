package com.timi.seulseul.data.api

import com.timi.seulseul.data.model.response.FcmTokenResponse
import com.timi.seulseul.data.model.resquest.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmTokenService {

    @POST("v1/fcm/check")
    suspend fun postFcmToken(@Body token : FcmTokenRequest) : Response<FcmTokenResponse>
}

