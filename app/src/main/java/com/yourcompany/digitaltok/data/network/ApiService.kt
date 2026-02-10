package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 서버 API와 통신하기 위한 Retrofit 인터페이스
 */
interface ApiService {
    @POST("devices")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<ApiResponse<DeviceData>>

    @GET("devices/{nfcUid}")
    suspend fun getDeviceByNfcUid(@Path("nfcUid") nfcUid: String): Response<ApiResponse<DeviceData>>

    @DELETE("devices/{nfcUid}")
    suspend fun deleteDevice(@Path("nfcUid") nfcUid: String): Response<ApiResponse<DeviceData>>

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
    suspend fun getImagePreview(@Path("imageId") imageId: Int): Response<ApiResponse<ImagePreview>>

    @GET("images/{imageId}/binary")
    suspend fun getImageBinaryInfo(@Path("imageId") imageId: Int): Response<ApiResponse<ImageBinaryInfo>>

    @Streaming
    @GET
    suspend fun downloadImageBinary(@Url url: String): Response<ResponseBody>
}
