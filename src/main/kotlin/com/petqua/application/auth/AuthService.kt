package com.petqua.application.auth

import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.auth.oauth.OauthClient
import com.petqua.domain.auth.oauth.OauthClientProvider
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.token.AuthToken
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.cart.CartProductRepository
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
    private val cartProductRepository: CartProductRepository,
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

        val member: Member = memberRepository.findByOauthIdAndOauthServerNumber(
            oauthUserInfo.oauthId,
            oauthServerType.number
        )?.let {
            updateTokenOrNothing(it, oauthClient)
        } ?: memberRepository.save(
            Member(
                oauthId = oauthUserInfo.oauthId,
                oauthServerNumber = oauthServerType.number,
                authority = MEMBER,
                oauthAccessToken = oauthTokenInfo.accessToken,
                expireAt = LocalDateTime.now().plusSeconds(oauthTokenInfo.expiresIn),
                oauthRefreshToken = oauthTokenInfo.refreshToken,
            )
        )
        val authToken = createAuthToken(member)
        return AuthTokenInfo(
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken,
        )
    }

    private fun updateTokenOrNothing(member: Member, oauthClient: OauthClient): Member {
        return if (member.hasExpiredToken()) {
            val refreshToken = member.oauthRefreshToken ?: throw MemberException(NOT_FOUND_MEMBER)
            val renewalOauthTokenInfo = oauthClient.updateToken(refreshToken)
            member.updateToken(renewalOauthTokenInfo)
        } else {
            member
        }
    }

    fun extendLogin(accessToken: String, refreshToken: String): AuthTokenInfo {
        validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
        val savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
            ?: throw AuthException(INVALID_REFRESH_TOKEN)
        if (savedRefreshToken.token != refreshToken) {
            throw AuthException(INVALID_REFRESH_TOKEN)
        }
        val member = memberRepository.findActiveByIdOrThrow(savedRefreshToken.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        val oauthClient = oauthClientProvider.getOauthClient(member.oauthServerType)
        updateTokenOrNothing(member, oauthClient)
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

    fun deleteBy(memberId: Long) {
        val member = memberRepository.findActiveByIdOrThrow(memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
        val oauthClient = oauthClientProvider.getOauthClient(member.oauthServerType)
        updateTokenOrNothing(member, oauthClient)

        val oauthAccessToken = member.oauthAccessToken ?: throw MemberException(NOT_FOUND_MEMBER)
        oauthClient.disconnect(oauthAccessToken)

        member.delete()

        cartProductRepository.deleteByMemberId(member.id)
        refreshTokenRepository.deleteByMemberId(member.id)
    }
}
