package com.petqua.domain.auth.token

import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType

class SignUpToken(
    private val signUpToken: String,
) : AuthToken() {

    override fun isSignUpToken(): Boolean {
        return true
    }

    override fun getAccessToken(): String {
        throw AuthException(AuthExceptionType.UNSUPPORTED_OPERATION)
    }

    override fun getRefreshToken(): String {
        throw AuthException(AuthExceptionType.UNSUPPORTED_OPERATION)
    }

    override fun getSignUpToken(): String {
        return signUpToken
    }
}
