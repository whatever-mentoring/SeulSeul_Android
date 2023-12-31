package com.timi.seulseul.data.model.response

data class GetEndLocation(
    val code: Int,
    val data: List<GetEndLocationData>
)

data class GetEndLocationData(
    val id: Int,
    val base_route_id: Long,
    val endX: Double,
    val endY: Double,
    val endNickName: String,
    val roadNameAddress: String,
    val jibunAddress: String
)
