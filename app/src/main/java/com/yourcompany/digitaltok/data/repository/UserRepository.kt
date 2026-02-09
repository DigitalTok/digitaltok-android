package com.yourcompany.digitaltok.data.repository

import android.content.Context
import android.util.Log
import com.yourcompany.digitaltok.data.model.UserMeResult
import com.yourcompany.digitaltok.data.network.RetrofitClient
import com.yourcompany.digitaltok.data.network.UserApiService

class UserRepository(
    private val context: Context
) {
    private val api: UserApiService =
        RetrofitClient.create(UserApiService::class.java)

    // 테스트 모드 스위치: 토큰이 아직 없을 때 UI/로직 연결 확인용
    // - true  : 서버 호출 없이 더미 닉네임을 반환 (UI에 뜨면 연동 성공)
    // - false : 실제 서버 호출 (토큰 필요)
    private val USE_FAKE_PROFILE_FOR_TEST = true

    suspend fun getMyProfile(): Result<UserMeResult> {
        return try {
            // (테스트) 서버 없이 성공 응답처럼 내려주기
            if (USE_FAKE_PROFILE_FOR_TEST) {
                Log.d("UserRepository", "TEST MODE: returning fake profile")
                return Result.success(
                    UserMeResult(
                        userId = 1L,
                        nickname = "테스트닉네임",
                        email = "test@example.com"
                    )
                )
            }

            // (실제) 서버 호출
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            val accessToken = prefs.getString("accessToken", null)

            if (accessToken.isNullOrBlank()) {
                return Result.failure(IllegalStateException("accessToken is missing"))
            }

            val res = api.getMyProfile("Bearer $accessToken")

            if (res.isSuccess && res.result != null) {
                Result.success(res.result)
            } else {
                Result.failure(
                    IllegalStateException(res.message ?: "Failed to load profile")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
