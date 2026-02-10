package com.yourcompany.digitaltok.data.repository

import android.content.Context

class PrefsAuthLocalStore(context: Context) : AuthLocalStore {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    override suspend fun getRefreshToken(): String? {
        return prefs.getString("refreshToken", null)
    }

    override suspend fun clearAuth() {
        prefs.edit()
            .remove("accessToken")
            .remove("refreshToken")
            .remove("email")
            .remove("nickname")
            .apply()
    }
}
