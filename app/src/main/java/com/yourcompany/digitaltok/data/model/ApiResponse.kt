package com.yourcompany.digitaltok.data.model

import com.google.gson.annotations.SerializedName

/**
 * 서버에서 오는 모든 응답을 감싸는 제네릭 데이터 클래스.
 * 가이드에 따라 status, code, message, data 필드를 가집니다.
 * @param <T> 응답의 'data' 필드에 들어갈 실제 데이터의 타입
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T?
)
