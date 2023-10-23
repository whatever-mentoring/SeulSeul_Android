package com.timi.seulseul.data.model.request

data class AlarmPostRequest(
    val base_route_id : Long,
    val alarmTime : Int,
    val alarmTerm : Int
)
