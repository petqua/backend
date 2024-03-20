package com.petqua.application.auth

import com.petqua.domain.auth.token.AuthToken
import com.petqua.presentation.auth.SignUpTokenResponse

data class AuthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
) {

    fun isSignUpNeeded(): Boolean {
        return AuthToken.isSignUpNeeded(refreshToken)
    }

    fun toSignUpTokenResponse(): SignUpTokenResponse {
        return SignUpTokenResponse(accessToken)
    }

    companion object {
        fun from(authToken: AuthToken): AuthTokenInfo {
            return AuthTokenInfo(
                accessToken = authToken.accessToken,
                refreshToken = authToken.refreshToken,
            )
        }

        fun signUpTokenOf(signUpToken: AuthToken): AuthTokenInfo {
            return AuthTokenInfo(
                accessToken = signUpToken.accessToken,
                refreshToken = signUpToken.refreshToken
            )
        }
    }
}
