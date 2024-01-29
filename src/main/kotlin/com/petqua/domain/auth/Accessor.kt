package com.petqua.domain.auth

import com.petqua.domain.auth.token.AccessTokenClaims

class Accessor(
    val memberId: Long,
    val authority: Authority,
) {

    companion object {
        fun from(accessTokenClaims: AccessTokenClaims): Accessor {
            return Accessor(
                memberId = accessTokenClaims.memberId,
                authority = accessTokenClaims.authority
            )
        }
    }
}
