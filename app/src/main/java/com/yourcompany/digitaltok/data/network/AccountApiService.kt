package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.ApiResponse
import com.yourcompany.digitaltok.data.model.EmailChangeRequest
import com.yourcompany.digitaltok.data.model.LogoutRequest
import retrofit2.http.PATCH
import retrofit2.http.Body
import retrofit2.http.DELETE

interface AccountApiService {

    // 로그아웃: refreshToken 필요
    @DELETE("auth/logout") // BASE_URL이 .../api/ 라서 앞에 /api 붙이면 중복됨
    suspend fun logout(@Body request: LogoutRequest): ApiResponse<String>

    // 회원탈퇴: 비밀번호 없이
    @DELETE("users/me")
    suspend fun withdraw(): ApiResponse<String>

    @PATCH("users/email")
    suspend fun changeEmail(@Body request: EmailChangeRequest): ApiResponse<String>
}
