package com.timi.seulseul.data.api

import com.timi.seulseul.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("v1/test")
    suspend fun postUser(@Body user: User): Response<User>

    @GET("v1/test/{id}")
    suspend fun getUser(@Path("id") id: String): Response<User>

    @PATCH("v1/test")
    suspend fun patchUser(@Body user: User): Response<User>

    @DELETE("v1/test/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Unit>
}