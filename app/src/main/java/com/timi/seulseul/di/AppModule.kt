package com.timi.seulseul.di

import com.timi.seulseul.MainApplication
import com.timi.seulseul.data.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "http://13.124.10.84:8080/"

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(5, TimeUnit.SECONDS) // 서버 연결 대기 최대 5초
            readTimeout(5, TimeUnit.SECONDS) // 데이터 읽기 대기 최대 5초
            writeTimeout(5, TimeUnit.SECONDS) // 데이터 쓰기 대기 최대 5초
        }
            .addInterceptor { chain ->
                val original = chain.request()

                // 년-월-일
                val date = LocalDate.now()
                // 요일
                val dayOfTheWeek =
                    date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

                // uuid 가져오기
                var uuid = MainApplication.prefs.getString("KEY_UUID", null)
                // 없다면 생성
                if (uuid == null) {
                    uuid = UUID.randomUUID().toString()
                    MainApplication.prefs.edit().putString("KEY_UUID", uuid).apply()
                }

                // Headers 객체 생성
                val headers = Headers.Builder()
                    .add("Auth", uuid)
                    .add("Day", dayOfTheWeek)
                    .build()

                val requestBuilder = original.newBuilder()
                    .headers(headers)
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

    }


    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

}