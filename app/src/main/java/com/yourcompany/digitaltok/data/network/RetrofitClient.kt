package com.yourcompany.digitaltok.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://3.37.213.174:8080/api/v1/"
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "accessToken"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Volatile
    private var appContext: Context? = null

    // Application.onCreate()에서 1번 호출
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun getAccessTokenFromPrefs(): String? {
        val ctx = appContext ?: return null
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    private val authenticatedOkHttpClient: OkHttpClient by lazy {
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val accessToken = getAccessTokenFromPrefs()

            val newRequest = originalRequest.newBuilder().apply {
                if (!accessToken.isNullOrBlank()) {
                    header("Authorization", "Bearer $accessToken")
                }
            }.build()

            chain.proceed(newRequest)
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val publicOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authenticatedOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val publicRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(publicOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val publicApiService: ApiService by lazy {
        publicRetrofit.create(ApiService::class.java)
    }

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun <T> createPublic(service: Class<T>): T {
        return publicRetrofit.create(service)
    }
}
