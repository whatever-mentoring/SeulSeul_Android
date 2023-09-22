package com.timi.seulseul.data.model


// Request 바디
data class Auth(
    val uuid: String
)

// Response 바디
data class AuthResponse(
    val code: Int,
    val data: AuthData
)

// Response - data 필드 내부 구조
data class AuthData(
    val uuid: String
)