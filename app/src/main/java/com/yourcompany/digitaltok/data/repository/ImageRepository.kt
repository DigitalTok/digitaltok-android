package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.*
import com.yourcompany.digitaltok.data.network.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.ResponseBody

class ImageRepository {
    // [수정] 두 종류의 apiService를 모두 가져옴
    private val apiService = RetrofitClient.apiService
    private val publicApiService = RetrofitClient.publicApiService // 인증이 필요 없는 요청용

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

    suspend fun getImagePreview(imageId: Int): Result<ImagePreview> {
        return try {
            val response = apiService.getImagePreview(imageId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing for preview."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error getting preview."))
                }
            } else {
                Result.failure(Exception("Failed to fetch image preview: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getImageBinaryInfo(imageId: Int): Result<ImageBinaryInfo> {
        return try {
            val response = apiService.getImageBinaryInfo(imageId)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.isSuccess) {
                    apiResponse.result?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("API success but result data is missing for binary info."))
                } else {
                    Result.failure(Exception(apiResponse?.message ?: "Unknown API error getting binary info."))
                }
            } else {
                Result.failure(Exception("Failed to fetch image binary info: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadImageBinary(url: String): Result<ResponseBody> {
        return try {
            // [수정] 인증이 필요 없는 publicApiService를 사용하여 이미지 다운로드
            val response = publicApiService.downloadImageBinary(url)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Download failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
