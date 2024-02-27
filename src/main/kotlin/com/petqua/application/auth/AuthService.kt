package com.petqua.application.auth

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.auth.oauth.OauthClientProvider
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.OauthUserInfo
import com.petqua.domain.auth.token.AuthToken
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.EXPIRED_REFRESH_TOKEN
import com.petqua.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import com.petqua.exception.auth.AuthExceptionType.NOT_RENEWABLE_ACCESS_TOKEN
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.time.LocalDateTime
import java.util.*

@Transactional
@Service
class AuthService(
    private val oauthClientProvider: OauthClientProvider,
    private val memberRepository: MemberRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    fun getAuthCodeRequestUrl(oauthServerType: OauthServerType): URI {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.getAuthCodeRequestUrl()
    }

    fun login(oauthServerType: OauthServerType, code: String): AuthTokenInfo {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        val oauthTokenInfo = oauthClient.requestToken(code)
        val oauthUserInfo = oauthClient.requestOauthUserInfo(oauthTokenInfo)
        val member = getMemberByOauthInfo(oauthUserInfo.oauthId, oauthServerType)
            ?: createMember(
                oauthTokenInfo = oauthTokenInfo,
                oauthUserInfo = oauthUserInfo,
                oauthServerType = oauthServerType
            )
        val authToken = createAuthToken(member)
        return AuthTokenInfo(
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken,
        )
    }

    fun extendLogin(accessToken: String, refreshToken: String): AuthTokenInfo {
        validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
        val savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
            ?: throw AuthException(INVALID_REFRESH_TOKEN)
        if (savedRefreshToken.token != refreshToken) {
            throw AuthException(INVALID_REFRESH_TOKEN)
        }
        val member = memberRepository.findByIdOrThrow(savedRefreshToken.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        val authToken = createAuthToken(member)
        return AuthTokenInfo(
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken,
        )
    }

    private fun validateTokenExpiredStatusForExtendLogin(accessToken: String, refreshToken: String) {
        if (!authTokenProvider.isExpiredAccessToken(accessToken)) {
            throw AuthException(NOT_RENEWABLE_ACCESS_TOKEN)
        }
        if (authTokenProvider.isExpiredRefreshToken(refreshToken)) {
            throw AuthException(EXPIRED_REFRESH_TOKEN)
        }
    }

    private fun getMemberByOauthInfo(oauthId: String, oauthServerType: OauthServerType): Member? {
        return memberRepository.findByOauthIdAndOauthServerNumber(oauthId, oauthServerType.number)
    }

    private fun createMember(
        oauthTokenInfo: OauthTokenInfo,
        oauthUserInfo: OauthUserInfo,
        oauthServerType: OauthServerType
    ): Member {
        return memberRepository.save(
            Member(
                oauthId = oauthUserInfo.oauthId,
                oauthServerNumber = oauthServerType.number,
                authority = MEMBER,
                oauthAccessToken = oauthTokenInfo.accessToken,
                expireAt = LocalDateTime.now().plusSeconds(oauthTokenInfo.expiresIn),
                oauthRefreshToken = oauthTokenInfo.refreshToken,
            )
        )
    }

    private fun createAuthToken(member: Member): AuthToken {
        val authToken = authTokenProvider.createAuthToken(member, Date())
        refreshTokenRepository.deleteByMemberId(member.id)
        refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = authToken.refreshToken
            )
        )
        return authToken
    }

    fun disconnect(oauthServerType: OauthServerType, accessToken: Member) {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)

    }
}
