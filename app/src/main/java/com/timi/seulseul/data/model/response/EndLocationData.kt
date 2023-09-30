package com.timi.seulseul.data.model.response

data class EndLocationResponse(
    val code: Int,
    val data: EndLocationData
)

data class EndLocationData(
    val id: Int,
    val base_route_id: Long,
    val endX: Double,
    val endY: Double,
    val endNickName: String,
    val roadNameAddress: String,
    val jibunAddress: String
)
