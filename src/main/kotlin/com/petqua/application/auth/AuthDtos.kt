package com.petqua.application.auth

import com.petqua.domain.auth.token.AuthToken

data class AuthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val isSignUpNeeded: Boolean,
) {
    companion object {
        fun from(authToken: AuthToken, isSignUpNeeded: Boolean): AuthTokenInfo {
            return AuthTokenInfo(
                accessToken = authToken.accessToken,
                refreshToken = authToken.refreshToken,
                isSignUpNeeded = isSignUpNeeded,
            )
        }
    }
}
