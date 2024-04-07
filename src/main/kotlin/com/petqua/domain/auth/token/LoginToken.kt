package com.petqua.domain.auth.token


import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.UNSUPPORTED_OPERATION

class LoginToken(
    private val accessToken: String,
    private val refreshToken: String,
) : AuthToken() {

    override fun isSignUpToken(): Boolean {
        return false
    }

    override fun getAccessToken(): String {
        return accessToken
    }

    override fun getRefreshToken(): String {
        return refreshToken
    }

    override fun getSignUpToken(): String {
        throw AuthException(UNSUPPORTED_OPERATION)
    }
}
