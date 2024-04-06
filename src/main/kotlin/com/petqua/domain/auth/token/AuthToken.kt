package com.petqua.domain.auth.token

private const val EMPTY_TOKEN = ""

class AuthToken private constructor(
    val accessToken: String,
    val refreshToken: String,
) {

    companion object {
        fun of(accessToken: String, refreshToken: String): AuthToken {
            return AuthToken(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        }

        fun signUpTokenOf(signUpToken: String): AuthToken {
            return AuthToken(
                accessToken = signUpToken,
                refreshToken = EMPTY_TOKEN
            )
        }

        fun isSignUpNeeded(token: String): Boolean {
            return token == EMPTY_TOKEN
        }
    }
}
