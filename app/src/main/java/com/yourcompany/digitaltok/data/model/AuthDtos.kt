package com.yourcompany.digitaltok.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResult(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)

data class SignupRequest(
    val email: String,
    val password: String,
    val phoneNumber: String
)

data class SignupResult(
    val userId: Long,
    val email: String,
    val nickname: String,
    val accessToken: String,
    val refreshToken: String
)

data class EmailRequest(val email: String)
data class LogoutRequest(val refreshToken: String)
