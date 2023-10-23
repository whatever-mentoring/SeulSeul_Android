package com.timi.seulseul.data.model.response

data class AlarmPatchResponse(
    val code : Int,
    val data : AlarmPatchData
)

data class AlarmPatchData(
    val id : Int,
    val alarmEnabled : Boolean,
    val alarmTime : Int,
    val alarmTerm : Int
)
