package com.petqua.domain.auth

import com.petqua.domain.auth.token.AccessTokenClaims

class LoginMember(
    val memberId: Long,
    val authority: Authority,
) {

    companion object {
        fun from(accessTokenClaims: AccessTokenClaims): LoginMember {
            return LoginMember(
                memberId = accessTokenClaims.memberId,
                authority = accessTokenClaims.authority
            )
        }
    }
}
