package com.petqua.presentation.auth

import io.swagger.v3.oas.annotations.media.Schema

data class AuthResponse(
    @Schema(description = "access token", example = "xxx.yyy.zzz")
    val accessToken: String,
)
