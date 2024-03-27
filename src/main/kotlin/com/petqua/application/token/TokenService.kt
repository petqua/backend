package com.petqua.application.token

import com.petqua.domain.auth.Authority

interface TokenService {

    fun createAuthOrSignUpToken(authMemberId: Long): AuthTokenInfo

    fun createAuthToken(memberId: Long, authority: Authority): AuthTokenInfo
}
