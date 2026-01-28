package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName

data class ImageUploadResult(
    @SerializedName("image")
    val image: ImageDetails,
    @SerializedName("imageMapping")
    val imageMapping: ImageMapping
)

data class ImageDetails(
    @SerializedName("imageId")
    val imageId: Int,
    @SerializedName("originalUrl")
    val originalUrl: String,
    @SerializedName("previewUrl")
    val previewUrl: String?,
    @SerializedName("einkDataUrl")
    val einkDataUrl: String?,
    @SerializedName("category")
    val category: String,
    @SerializedName("imageName")
    val imageName: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("deletedAt")
    val deletedAt: String?,
    @SerializedName("subwayTemplateId")
    val subwayTemplateId: Int
)

data class ImageMapping(
    @SerializedName("userImageId")
    val userImageId: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("imageId")
    val imageId: Int,
    @SerializedName("isFavorite")
    val isFavorite: Boolean,
    @SerializedName("savedAt")
    val savedAt: String,
    @SerializedName("lastUsedAt")
    val lastUsedAt: String
)

/**
 * GET /images/recent API의 `result` 필드에 해당하는 데이터 클래스
 */
data class RecentImagesResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<RecentImage>
)

/**
 * GET /images/recent API 응답의 `items` 배열에 포함된 개별 이미지 정보
 */
data class RecentImage(
    @SerializedName("imageId")
    val imageId: Int,
    @SerializedName("previewUrl")
    val previewUrl: String,
    @SerializedName("imageName")
    val imageName: String,
    @SerializedName("isFavorite")
    val isFavorite: Boolean,
    @SerializedName("lastUsedAt")
    val lastUsedAt: String
)

data class ImagePreview(
    @SerializedName("imageId")
    val imageId: Int,
    @SerializedName("previewUrl")
    val previewUrl: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)
