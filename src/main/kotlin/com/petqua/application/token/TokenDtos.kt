package com.petqua.application.token

import com.petqua.domain.auth.token.AuthToken

data class AuthTokenInfo(
    val authToken: AuthToken,
) {
    val accessToken: String
        get() = authToken.getAccessToken()

    val refreshToken: String
        get() = authToken.getRefreshToken()

    val signUpToken: String
        get() = authToken.getSignUpToken()

    fun isSignUpNeeded(): Boolean {
        return authToken.isSignUpNeeded()
    }
}
