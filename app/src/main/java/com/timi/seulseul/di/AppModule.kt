package com.timi.seulseul.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun providesConvertorFactory() =
        json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun providesOkHttpClient() : OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            connectTimeout(5, TimeUnit.SECONDS)
            readTimeout(5, TimeUnit.SECONDS)
            writeTimeout(5, TimeUnit.SECONDS)
        }
    }

    @Provides
    @Singleton
    @RetrofitSeulSeul
    fun providesRetrofit(
        client: OkHttpClient.Builder,
        converterFactory: Converter.Factory,
    ) : Retrofit {
        return Retrofit.Builder()
            .baseUrl("<http://13.125.156.65:8080/v1/>")
            .addConverterFactory(converterFactory)
            .client(client.build())
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitSeulSeul