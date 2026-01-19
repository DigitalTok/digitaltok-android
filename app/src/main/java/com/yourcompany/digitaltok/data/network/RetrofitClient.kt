package com.yourcompany.digitaltok.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 인스턴스를 관리하는 싱글톤 객체
 * 가이드에 따라 HttpLoggingInterceptor를 추가하여 통신 과정을 로그로 확인할 수 있도록 설정
 */
object RetrofitClient {

    // 서버의 기본 URL
    private const val BASE_URL = "http://3.37.213.174:8080/api/"

    // 로그 출력을 위한 인터셉터
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 요청과 응답의 모든 내용을 로그로 출력
    }

    // OkHttp 클라이언트 설정 (인터셉터, 타임아웃 등)
    private val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Retrofit 인스턴스 생성
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttpClient) // 위에서 설정한 OkHttp 클라이언트를 사용
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ApiService 구현체 제공
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
