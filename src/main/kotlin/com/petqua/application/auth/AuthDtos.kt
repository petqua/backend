package com.petqua.application.auth

data class AuthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
)
