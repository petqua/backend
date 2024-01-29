package com.petqua.application.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
)