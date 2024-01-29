package com.petqua.domain.auth.token

import com.petqua.common.exception.auth.AuthException
import com.petqua.common.exception.auth.AuthExceptionType.INVALID_ACCESS_TOKEN
import com.petqua.domain.auth.Authority

private const val MEMBER_ID = "memberId"
private const val AUTHORITY = "authority"

class AccessTokenClaims(
    val memberId: Long,
    val authority: Authority,
) {

    fun getClaims(): Map<String, String> {
        return mutableMapOf(MEMBER_ID to memberId.toString(), AUTHORITY to authority.name)
    }

    companion object {
        fun from(claims: Map<String, String>): AccessTokenClaims {
            val memberId = claims[MEMBER_ID]
                ?: throw AuthException(INVALID_ACCESS_TOKEN)
            val authority = claims[AUTHORITY]
                ?: throw AuthException(INVALID_ACCESS_TOKEN)

            return AccessTokenClaims(
                memberId = memberId.toLong(),
                authority = Authority.from(authority)
            )
        }
    }
}
