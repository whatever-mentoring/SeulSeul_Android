package com.timi.seulseul.data.model

// Request Body
data class Location(
    val id: Long,
    val startX: String,
    val startY: String,
    val dayInfo: String
)

// PATCH Request Body
data class PatchLocation(
    val id: Long,
    val startX: String,
    val startY: String
)

// Response Body
data class LocationResponse(
    val code: Int,
    val data: LocationData
)

// Response - data 필드 내부 구조
data class LocationData(
    val id: Long,
    val startX: String,
    val startY: String,
    val dayInfo: String
)