package com.timi.seulseul.data.model.response

data class AlarmResponse(
    val code : Int,
    val data : AlarmData
)

data class AlarmData(
    val id : Int,
    val alarmEnabled : Boolean,
    val alarmTime : Int,
    val alarmTerm : Int
)
