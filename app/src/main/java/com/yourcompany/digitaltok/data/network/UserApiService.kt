package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.ApiResponse
import com.yourcompany.digitaltok.data.model.UserMeResult
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApiService {

    // GET /api/users/me
    @GET("/api/users/me")
    suspend fun getMyProfile(
        @Header("Authorization") authorization: String
    ): ApiResponse<UserMeResult>
}
