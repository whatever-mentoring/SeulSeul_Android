package com.timi.seulseul.data.api

import com.timi.seulseul.data.model.response.SubwayRouteResponse
import retrofit2.Response
import retrofit2.http.GET

interface SubwayRouteService {
    @GET("v1/route/detail")
    suspend fun getSubwayRoute() : Response<SubwayRouteResponse>
}