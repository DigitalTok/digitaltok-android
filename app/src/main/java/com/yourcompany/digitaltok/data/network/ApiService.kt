package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 서버 API와 통신하기 위한 Retrofit 인터페이스
 *
 * ✅ Auth는 "/api/v1/..." 절대경로로 고정 (baseUrl에 /api 포함 여부 때문에 404 나는 것 방지)
 */
interface ApiService {

    // ==========================
    // Auth (인증)
    // ==========================

    // 회원가입: POST /api/v1/auth/signup
    @POST("/api/v1/auth/signup")
    suspend fun signup(
        @Body body: SignupRequest
    ): Response<ApiResponse<SignupResult>>

    // 로그인: POST /api/v1/auth/login
    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<ApiResponse<LoginResult>>

    // 이메일 중복 확인: POST /api/v1/auth/duplicate-check
    @POST("/api/v1/auth/duplicate-check")
    suspend fun duplicateCheck(
        @Body body: DuplicateCheckRequest
    ): Response<ApiResponse<String>>

    // 비밀번호 재설정: POST /api/v1/auth/password/reset
    @POST("/api/v1/auth/password/reset")
    suspend fun passwordReset(
        @Body body: PasswordResetRequest
    ): Response<ApiResponse<String>>

    // 로그아웃: DELETE /api/v1/auth/logout (Body 있음)
    @HTTP(method = "DELETE", path = "/api/v1/auth/logout", hasBody = true)
    suspend fun logout(
        @Body body: LogoutRequest
    ): Response<ApiResponse<String>>


    // ==========================
    // Devices (기존)
    // ==========================
    // ⚠️ 여기 경로는 서버가 /api/v1/devices 인지 확인 필요.
    // 보통은 /api/v1/devices 이라서 아래처럼 맞춰두는 게 안전함.
    @POST("/api/v1/devices")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<ApiResponse<DeviceData>>

    @GET("/api/v1/devices/{deviceId}")
    suspend fun getDeviceById(@Path("deviceId") deviceId: Int): Response<ApiResponse<DeviceData>>

    @DELETE("/api/v1/devices/{deviceId}")
    suspend fun deleteDevice(@Path("deviceId") deviceId: Int): Response<ApiResponse<DeviceData>>


    // ==========================
    // Images (기존)
    // ==========================
    // ⚠️ Images는 서버 스펙이 제각각이라 기존 유지 (필요하면 Swagger 기준으로 맞추면 됨)
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

    // 기존 코드 유지 (서버가 진짜 /api/images/... 인지 확인 필요)
    @GET("api/images/{imageId}/preview")
    suspend fun getImagePreview(@Path("imageId") imageId: Int): Response<ApiResponse<ImagePreview>>
}
