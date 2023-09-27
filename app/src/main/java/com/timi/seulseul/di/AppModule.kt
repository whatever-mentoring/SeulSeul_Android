package com.timi.seulseul.di

import android.content.Context
import android.content.SharedPreferences
import com.timi.seulseul.MainApplication
import com.timi.seulseul.data.api.AlarmService
import com.timi.seulseul.data.api.ApiService
import com.timi.seulseul.data.api.FcmTokenService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Dagger Hilt를 사용해서 앱 전체에 공유되는 객체들을 제공하는 모듈!
@Module
// 싱글톤 -> 앱 생명주기와 같은 생명주기를 갖게됨
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://13.209.31.233:8080/"

    // @Provides < - Dagger Hilt에서 객체 생성 로직 제공
    // @Singleton < - 앱 전체에서 오직 하나만 존재

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS) // 서버 연결 대기 최대 10초
            readTimeout(30, TimeUnit.SECONDS) // 데이터 읽기 대기 최대 30초
            writeTimeout(15, TimeUnit.SECONDS) // 데이터 쓰기 대기 최대 15초
        }
            .addInterceptor(HeaderInterceptor())
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        // OkHttp
        .client(okHttpClient)
        // Json 응답 파싱
        .addConverterFactory(GsonConverterFactory.create()).build()


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideFcmTokenService(retrofit: Retrofit): FcmTokenService = retrofit.create(FcmTokenService::class.java)

    @Provides
    @Singleton
    fun provideAlarmService(retrofit: Retrofit): AlarmService = retrofit.create(AlarmService::class.java)
}

//HeaderInterceptor 분리
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // uuid 가져오기
        var uuid = MainApplication.prefs.getString("KEY_UUID", null)
        // 없다면 생성
        if (uuid == null) {
            uuid = UUID.randomUUID().toString()
            MainApplication.prefs.edit().putString("KEY_UUID", uuid).apply()
        }

        // Headers 객체 생성
        val headers = Headers.Builder().add("Auth", uuid).build()

        val requestBuilder =
            original.newBuilder().headers(headers).method(original.method, original.body)

        val request = requestBuilder.build()

        return chain.proceed(request)
    }
}