package com.petqua.application.auth

import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.OauthUserInfo
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.auth.token.findByTokenOrThrow
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import java.util.Date
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val cartProductRepository: CartProductRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    fun findOrCreateMemberBy(
        oauthServerType: OauthServerType,
        oauthTokenInfo: OauthTokenInfo,
        oauthUserInfo: OauthUserInfo
    ): Member {
        val member = findOrSaveMemberBy(oauthServerType, oauthUserInfo)
        member.updateOauthToken(
            accessToken = oauthTokenInfo.accessToken,
            expiresIn = oauthTokenInfo.expiresIn,
            refreshToken = oauthTokenInfo.refreshToken
        )
        return member
    }

    private fun findOrSaveMemberBy(
        oauthServerType: OauthServerType,
        oauthUserInfo: OauthUserInfo
    ): Member {
        return memberRepository.findByOauthIdAndOauthServerNumberAndIsDeletedFalse(
            oauthId = oauthUserInfo.oauthId,
            oauthServerNumber = oauthServerType.number
        ) ?: memberRepository.save(
            Member(
                oauthId = oauthUserInfo.oauthId,
                profileImageUrl = oauthUserInfo.imageUrl,
                oauthServerNumber = oauthServerType.number,
                authority = MEMBER,
            )
        )
    }

    fun createAuthToken(member: Member): AuthTokenInfo {
        val authToken = authTokenProvider.createAuthToken(member, Date())
        refreshTokenRepository.deleteByMemberId(member.id)
        refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = authToken.refreshToken
            )
        )
        return AuthTokenInfo.from(authToken)
    }

    fun validateTokenExpiredStatusForExtendLogin(accessToken: String, refreshToken: String) {
        authTokenProvider.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun findMemberBy(accessToken: String, refreshToken: String): Member {
        val savedRefreshToken = refreshTokenRepository.findByTokenOrThrow(refreshToken) {
            AuthException(INVALID_REFRESH_TOKEN)
        }
        savedRefreshToken.validateTokenValue(refreshToken)
        return memberRepository.findActiveByIdOrThrow(savedRefreshToken.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    @Transactional(readOnly = true)
    fun findMemberBy(memberId: Long): Member {
        return memberRepository.findActiveByIdOrThrow(memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    fun updateOauthToken(member: Member, oauthTokenInfo: OauthTokenInfo) {
        member.updateOauthToken(
            accessToken = oauthTokenInfo.accessToken,
            expiresIn = oauthTokenInfo.expiresIn,
            refreshToken = oauthTokenInfo.refreshToken
        )
        memberRepository.save(member)
    }

    fun delete(member: Member) {
        member.delete()
        memberRepository.save(member)

        cartProductRepository.deleteByMemberId(member.id)
        refreshTokenRepository.deleteByMemberId(member.id)
    }

    fun signOut(member: Member) {
        member.signOut()
        memberRepository.save(member)
        
        refreshTokenRepository.deleteByMemberId(member.id)
    }
}
