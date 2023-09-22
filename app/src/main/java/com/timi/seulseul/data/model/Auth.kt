package com.timi.seulseul.data.model

data class Auth(
    val uuid: String
)

data class AuthResponse(
    val code: Int,
    val data: AuthData
)

data class AuthData(
    val uuid: String
)