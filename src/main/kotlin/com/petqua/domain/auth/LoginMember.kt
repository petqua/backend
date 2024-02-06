package com.petqua.domain.auth

import com.petqua.domain.auth.token.AccessTokenClaims
import io.swagger.v3.oas.annotations.Hidden

@Hidden
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
