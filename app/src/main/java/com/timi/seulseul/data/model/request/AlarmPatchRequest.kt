package com.timi.seulseul.data.model.request

data class AlarmPatchRequest(
    val id : Int,
    val base_route_id : Int,
    val alarmTime : Int,
    val alarmTerm : Int
)
