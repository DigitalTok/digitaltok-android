package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 서버 API와 통신하기 위한 Retrofit 인터페이스
 * ✔ 기존 기능 + Auth(회원가입/로그인/로그아웃 등) 포함 최종본
 */
interface ApiService {

    /* =========================
     * Auth (인증)
     * ========================= */

    // 회원가입
    // POST /api/v1/auth/signup
    @POST("v1/auth/signup")
    suspend fun signup(
        @Body body: SignupRequest
    ): Response<ApiResponse<SignupResult>>

    // 로그인
    // POST /api/v1/auth/login
    @POST("v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<ApiResponse<LoginResult>>

    // 이메일 중복 확인
    // POST /api/v1/auth/duplicate-check
    @POST("v1/auth/duplicate-check")
    suspend fun duplicateCheck(
        @Body body: EmailRequest
    ): Response<ApiResponse<String>>

    // 비밀번호 재설정
    // POST /api/v1/auth/password/reset
    @POST("v1/auth/password/reset")
    suspend fun passwordReset(
        @Body body: EmailRequest
    ): Response<ApiResponse<String>>

    // 로그아웃 (DELETE + body)
    // DELETE /api/v1/auth/logout
    @HTTP(method = "DELETE", path = "v1/auth/logout", hasBody = true)
    suspend fun logout(
        @Body body: LogoutRequest
    ): Response<ApiResponse<String>>


    /* =========================
     * Device
     * ========================= */

    @POST("v1/devices")
    suspend fun registerDevice(
        @Body request: DeviceRegistrationRequest
    ): Response<ApiResponse<DeviceData>>

    @GET("v1/devices/{deviceId}")
    suspend fun getDeviceById(
        @Path("deviceId") deviceId: Int
    ): Response<ApiResponse<DeviceData>>

    @DELETE("v1/devices/{deviceId}")
    suspend fun deleteDevice(
        @Path("deviceId") deviceId: Int
    ): Response<ApiResponse<DeviceData>>


    /* =========================
     * Image
     * ========================= */

    @Multipart
    @POST("images")
    suspend fun uploadImage(
        @Query("imageName") imageName: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<ImageUploadResult>>

    @PATCH("images/{imageId}/favorite")
    suspend fun updateFavoriteStatus(
        @Path("imageId") imageId: String,
        @Body payload: Map<String, Boolean>
    ): Response<ApiResponse<Unit>>

    @GET("images/recent")
    suspend fun getRecentImages(): Response<ApiResponse<RecentImagesResponse>>

    @GET("images/{imageId}/preview")
    suspend fun getImagePreview(
        @Path("imageId") imageId: Int
    ): Response<ApiResponse<ImagePreview>>
}
