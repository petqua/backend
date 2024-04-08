package com.petqua.application.auth

import com.petqua.application.token.AuthTokenInfo
import com.petqua.application.token.TokenService
import com.petqua.domain.auth.Authority
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Transactional
@Service
class AuthTokenService(
    private val authTokenProvider: AuthTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
) : TokenService {

    override fun createAuthOrSignUpToken(authCredentialsId: Long): AuthTokenInfo {
        val member = memberRepository.findByAuthCredentialsId(authCredentialsId)
        return member?.let {
            createAuthToken(
                memberId = member.id,
                authority = member.authority
            )
        } ?: createSignUpToken(authCredentialsId)
    }

    override fun createAuthToken(memberId: Long, authority: Authority): AuthTokenInfo {
        val authToken = authTokenProvider.createLoginAuthToken(memberId, authority, Date())
        refreshTokenRepository.deleteByMemberId(memberId)
        refreshTokenRepository.save(
            RefreshToken(
                memberId = memberId,
                token = authToken.getRefreshToken()
            )
        )
        return AuthTokenInfo(authToken)
    }

    private fun createSignUpToken(authCredentialsId: Long): AuthTokenInfo {
        val authToken = authTokenProvider.createSignUpAuthToken(authCredentialsId, Date())
        return AuthTokenInfo(authToken)
    }
}
