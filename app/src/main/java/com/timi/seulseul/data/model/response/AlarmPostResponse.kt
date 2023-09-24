package com.timi.seulseul.data.model.response

data class AlarmPostResponse(
    val code : Int,
    val data : AlarmPostData
)

data class AlarmPostData(
    val id : Int,
    val alarmEnabled : Boolean,
    val alarmTime : Int,
    val alarmTerm : Int
)
