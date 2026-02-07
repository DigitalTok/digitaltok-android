package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.LogoutRequest
import com.yourcompany.digitaltok.data.network.AccountApiService
import com.yourcompany.digitaltok.data.model.EmailChangeRequest

class AccountRepository(
    private val api: AccountApiService,
    private val authLocalStore: AuthLocalStore
) {
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
        val res = api.changeEmail(EmailChangeRequest(password, newEmail))
        if (!res.isSuccess) throw RuntimeException(res.message)
    }

}
