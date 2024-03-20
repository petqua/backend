package com.petqua.presentation.auth

import io.swagger.v3.oas.annotations.media.Schema

data class RedirectUriResponse(
    @Schema(
        description = "redirect uri",
        example = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=342deee50e41fa203db2f1ea32eb589c&redirect_uri=http://localhost:8080/auth/login/kakao"
    )
    val uri: String,
)

data class SignUpTokenResponse(
    @Schema(
        description = "회원가입 api 전용 임시 토큰",
        example = "xxx.yyy.zzz"
    )
    val signUpToken: String,
)
