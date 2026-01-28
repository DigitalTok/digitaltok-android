package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.ImageUploadResult
import com.yourcompany.digitaltok.data.model.RecentImagesResponse
import com.yourcompany.digitaltok.data.network.RetrofitClient
import okhttp3.MultipartBody

class ImageRepository {
    private val apiService = RetrofitClient.apiService
    suspend fun uploadImage(
        imageName: String,
        imageFile: MultipartBody.Part
    ): Result<ImageUploadResult> {
        return try {
            val response = apiService.uploadImage(imageName, imageFile)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it) // 성공 시 Result.success 반환
                    } ?: Result.failure(Exception("API success but result data is missing."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error"))
                }
            } else {
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e) // 모든 예외를 Result.failure로 감싸서 반환
        }
    }

    suspend fun updateFavoriteStatus(imageId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            val response = apiService.updateFavoriteStatus(imageId, mapOf("isFavorite" to isFavorite))
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentImages(): Result<RecentImagesResponse> {
        return try {
            val response = apiService.getRecentImages()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error"))
                }
            } else {
                Result.failure(Exception("Failed to fetch recent images: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
