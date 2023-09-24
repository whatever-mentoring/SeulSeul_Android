package com.timi.seulseul.data.repository

object ErrorUtils {
    // http 응답 상태 코드에 따로 관리
    fun getErrorMessage(code: Int): String {
        return when(code) {
            400 -> "Bad Request"
            404 -> "Not Found"
            409 -> "Conflict"
            500 -> "Internal Server Error"
            -8 -> "필수 입력값 형식 및 범위 오류"
            -9 -> "필수 입력값 누락"
            410 -> "USER_NOT_FOUND"
            411 -> "BASEROUTE_NOT_FOUND"
            412 -> "ALARM_NOT_FOUND"
            else -> "Unknown error occurred with code: $code"
        }
    }
}