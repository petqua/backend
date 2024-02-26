package com.petqua.application.auth

import com.petqua.domain.auth.token.AuthToken

data class AuthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun from(authToken: AuthToken): AuthTokenInfo {
            return AuthTokenInfo(
                accessToken = authToken.accessToken,
                refreshToken = authToken.refreshToken,
            )
        }
    }
}
