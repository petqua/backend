package com.petqua.domain.auth.token

import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.INVALID_ACCESS_TOKEN

private const val AUTH_MEMBER_ID = "authCredentialsId"

class SignUpTokenClaims(
    val authCredentialsId: Long,
) {

    fun getClaims(): Map<String, String> {
        return mapOf(AUTH_MEMBER_ID to authCredentialsId.toString())
    }

    companion object {
        fun from(claims: Map<String, String>): SignUpTokenClaims {
            val authCredentialsId = claims[AUTH_MEMBER_ID]
                ?: throw AuthException(INVALID_ACCESS_TOKEN)

            return SignUpTokenClaims(
                authCredentialsId = authCredentialsId.toLong()
            )
        }
    }
}
