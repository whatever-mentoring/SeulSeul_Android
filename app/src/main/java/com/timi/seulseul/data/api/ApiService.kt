package com.timi.seulseul.data.api

import com.timi.seulseul.data.model.Auth
import com.timi.seulseul.data.model.AuthResponse
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


    // uuid 비회원 식별
    // endpoint등록 -> Retrofit에서 OkHttp Client를 사용해 서버에 POST 요청을 보낸다.
    @POST("v1/user")
    // @Body 어노테이션이 붙으면 HTTP request 본문에 해당된다.
    // auth: Auth가 본문으로 사용되고 <- json 형식으로 변환 후 서버로 전송된다.
    // 메소드의 반환타입 -> Response<T>, T는 서버에서 받아온 response을 모델링하는 데이터 클래스 (Auth.kt참고)
    // suspend fun <- 코루틴에서 실행해야함, 네트워크 작업 비동기적으로 처리
    suspend fun postAuth(@Body auth: Auth): Response<AuthResponse>

}