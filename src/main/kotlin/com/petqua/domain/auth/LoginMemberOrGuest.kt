package com.petqua.domain.auth

import com.petqua.domain.auth.Authority.GUEST
import com.petqua.domain.auth.token.AccessTokenClaims
import io.swagger.v3.oas.annotations.Hidden
import kotlin.Long.Companion.MIN_VALUE

@Hidden
class LoginMemberOrGuest(
    val memberId: Long,
    val authority: Authority,
) {

    fun isMember(): Boolean {
        return this != GUEST_INSTANCE;
    }

    companion object {

        private val GUEST_INSTANCE = LoginMemberOrGuest(MIN_VALUE, GUEST)

        fun getMemberFrom(accessTokenClaims: AccessTokenClaims): LoginMemberOrGuest {
            return LoginMemberOrGuest(
                memberId = accessTokenClaims.memberId,
                authority = accessTokenClaims.authority,
            )
        }

        fun getGuest(): LoginMemberOrGuest {
            return GUEST_INSTANCE
        }
    }
}
