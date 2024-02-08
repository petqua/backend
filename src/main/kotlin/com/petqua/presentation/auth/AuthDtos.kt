package com.petqua.presentation.auth

import io.swagger.v3.oas.annotations.media.Schema

data class AuthResponse(
    @Schema(description = "access token", example = "xxx.yyy.zzz")
    val accessToken: String,
)

data class RedirectUriResponse(
    @Schema(
        description = "redirect uri",
        example = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=342deee50e41fa203db2f1ea32eb589c&redirect_uri=http://localhost:8080/auth/login/kakao"
    )
    val uri: String,
)
