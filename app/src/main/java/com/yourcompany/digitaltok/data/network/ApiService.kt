package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.ApiResponse
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.model.DeviceRegistrationRequest
import com.yourcompany.digitaltok.data.model.ImageUploadResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 서버 API와 통신하기 위한 Retrofit 인터페이스
 */
interface ApiService {
    @POST("v1/devices")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<ApiResponse<DeviceData>>

    @GET("v1/devices/{deviceId}")
    suspend fun getDeviceById(@Path("deviceId") deviceId: Int): Response<ApiResponse<DeviceData>>

    @DELETE("v1/devices/{deviceId}")
    suspend fun deleteDevice(@Path("deviceId") deviceId: Int): Response<ApiResponse<DeviceData>>

    @Multipart
    @POST("images")
    suspend fun uploadImage(
        @Query("imageName") imageName: String,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<ImageUploadResult>>
}
