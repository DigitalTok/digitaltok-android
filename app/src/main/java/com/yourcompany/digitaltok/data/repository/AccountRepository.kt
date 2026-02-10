package com.yourcompany.digitaltok.data.repository

import android.util.Log
import com.yourcompany.digitaltok.data.model.LogoutRequest
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.model.EmailChangeRequest

class AccountRepository(
    private val api: AccountApiService,
    private val authLocalStore: AuthLocalStore
) {
    // 추가: 이메일 변경 테스트 모드 스위치
    // true  -> 서버 호출 없이 성공 처리(UI/플로우 테스트 가능)
    // false -> 실제 서버 호출
    private val USE_FAKE_CHANGE_EMAIL_FOR_TEST = true

    suspend fun logout(): Result<Unit> = runCatching {
        val refresh = authLocalStore.getRefreshToken()
            ?: throw IllegalStateException("refreshToken is null")

        val res = api.logout(LogoutRequest(refresh))
        if (!res.isSuccess) throw RuntimeException(res.message)

        authLocalStore.clearAuth()
    }

    suspend fun withdraw(): Result<Unit> = runCatching {
        val res = api.withdraw()
        if (!res.isSuccess) throw RuntimeException(res.message)

        authLocalStore.clearAuth()
    }

    suspend fun changeEmail(password: String, newEmail: String): Result<Unit> = runCatching {
        // 추가: 테스트 모드면 서버 없이 성공 처리
        if (USE_FAKE_CHANGE_EMAIL_FOR_TEST) {
            Log.d("AccountRepository", "TEST MODE: changeEmail success (password=$password, newEmail=$newEmail)")
            return@runCatching Unit
        }

        // 기존 로직 유지
        val res = api.changeEmail(EmailChangeRequest(password, newEmail))
        if (!res.isSuccess) throw RuntimeException(res.message)
    }
}
