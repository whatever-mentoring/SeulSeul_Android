package com.timi.seulseul.data.model.response

data class Response<T>(
    val code: Int,
    val data: LocationResponse
)

data class LocationResponse(
    val id: Int,
    val base_route_id: Int,
    val endX: Double,
    val endY: Double,
    val endNickName: String,
    val roadNameAddress: String,
    val jibunAddress: String
)
