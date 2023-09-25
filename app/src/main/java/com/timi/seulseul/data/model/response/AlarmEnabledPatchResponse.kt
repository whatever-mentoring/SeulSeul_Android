package com.timi.seulseul.data.model.response

data class AlarmEnabledPatchResponse(
    val code : Int,
    val data : AlarmEnabledData
)

data class AlarmEnabledData(
    val id : Int,
    val alarmEnabled : Boolean,
    val alarmTime : Int,
    val alarmTerm : Int
)
