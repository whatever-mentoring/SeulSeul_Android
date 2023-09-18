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

    //GET 서버에서 데이터를 가져옴으로 @Body가 필요없다.
    @GET("v1/test")
    suspend fun getUsers(): Response<List<User>>

    //POST 새로운 데이터를 생성하는 것이기 때문에 @Body가 필요하다!
    @POST("v1/test")
    suspend fun postUser(@Body user: User): Response<User>

    //DELETE id값이 글 개수니깐 하나씩 삭제해야지 ㅇㅇ
    @DELETE("v1/test/{id}")
    suspend fun deleteUser(@Path("id") id: String?): Response<Unit>

    //PATCH 수정할 데이터만 본문에 포함시킨다!
    @PATCH("v1/test")
    suspend fun patchUser(@Body user: User): Response<User>


}