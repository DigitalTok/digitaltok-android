package com.yourcompany.digitaltok.data.repository

import android.util.Log
import com.yourcompany.digitaltok.data.model.EmailChangeRequest
import com.yourcompany.digitaltok.data.model.LogoutRequest
import com.yourcompany.digitaltok.data.network.AccountApiService

class AccountRepository(
    private val api: AccountApiService,
    private val authLocalStore: AuthLocalStore
) {
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
        if (USE_FAKE_CHANGE_EMAIL_FOR_TEST) {
            Log.d("AccountRepository", "TEST MODE: changeEmail success (password=$password, newEmail=$newEmail)")
            return@runCatching Unit
        }

        val res = api.changeEmail(EmailChangeRequest(password, newEmail))
        if (!res.isSuccess) throw RuntimeException(res.message)
    }
}
