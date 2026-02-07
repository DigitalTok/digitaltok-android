package com.yourcompany.digitaltok.data.repository

interface AuthLocalStore {
    suspend fun getRefreshToken(): String?
    suspend fun clearAuth()
}
