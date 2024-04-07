package com.petqua.domain.auth.token

abstract class AuthToken {

    protected abstract fun isSignUpToken(): Boolean

    abstract fun getAccessToken(): String

    abstract fun getRefreshToken(): String

    abstract fun getSignUpToken(): String

    fun isSignUpNeeded(): Boolean {
        return isSignUpToken()
    }

    companion object {
        fun loginTokenOf(accessToken: String, refreshToken: String): AuthToken {
            return LoginToken(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }

        fun signUpTokenOf(signUpToken: String): AuthToken {
            return SignUpToken(
                signUpToken = signUpToken,
            )
        }
    }
}
