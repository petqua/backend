package com.petqua.application.auth

import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.domain.auth.AuthMember
import com.petqua.domain.auth.AuthMemberRepository
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.BlackListTokenCacheStorage
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.auth.token.findByTokenOrThrow
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.findByAuthMemberIdOrThrow
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Transactional
@Service
class AuthService(
    private val authMemberRepository: AuthMemberRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val cartProductRepository: CartProductRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val blackListTokenCacheStorage: BlackListTokenCacheStorage,
) {

    fun findOrCreateAuthMemberBy(
        oauthServerType: OauthServerType,
        oauthId: Long,
    ): AuthMember {
        return authMemberRepository.findByOauthIdAndOauthServerNumberAndIsDeletedFalse(
            oauthId = oauthId,
            oauthServerNumber = oauthServerType.number
        ) ?: authMemberRepository.save(
            AuthMember.authMemberOf(
                oauthId = oauthId,
                oauthServerNumber = oauthServerType.number,
            )
        )
    }

    fun findMemberBy(authMember: AuthMember): Member? {
        return memberRepository.findByAuthMemberId(authMember.id)
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

    fun createSignUpAuthToken(authMember: AuthMember): AuthTokenInfo {
        val authToken = authTokenProvider.createSignUpAuthToken(authMember, Date())
        return AuthTokenInfo.signUpTokenOf(authToken)
    }

    fun validateTokenExpiredStatusForExtendLogin(accessToken: String, refreshToken: String) {
        authTokenProvider.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun findAuthMemberBy(accessToken: String, refreshToken: String): AuthMember {
        authTokenProvider.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
        val savedRefreshToken = refreshTokenRepository.findByTokenOrThrow(refreshToken) {
            AuthException(INVALID_REFRESH_TOKEN)
        }
        savedRefreshToken.validateTokenValue(refreshToken)
        return authMemberRepository.findActiveByIdOrThrow(savedRefreshToken.memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    @Transactional(readOnly = true)
    fun findMemberBy(memberId: Long): Member {
        return memberRepository.findActiveByIdOrThrow(memberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    @Transactional(readOnly = true)
    fun findAuthMemberBy(authMemberId: Long): AuthMember {
        return authMemberRepository.findActiveByIdOrThrow(authMemberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    fun updateOauthToken(authMember: AuthMember, oauthTokenInfo: OauthTokenInfo) {
        authMember.updateOauthToken(
            accessToken = oauthTokenInfo.accessToken,
            expiresIn = oauthTokenInfo.expiresIn,
            refreshToken = oauthTokenInfo.refreshToken
        )
        authMemberRepository.save(authMember)
    }

    fun delete(member: Member, authMember: AuthMember) {
        member.delete()
        memberRepository.save(member)
        authMember.delete()
        authMemberRepository.save(authMember)

        cartProductRepository.deleteByMemberId(authMember.id)
        refreshTokenRepository.deleteByMemberId(authMember.id)
    }

    @Transactional(readOnly = true)
    fun findMemberByAuthMemberId(authMemberId: Long): Member {
        return memberRepository.findByAuthMemberIdOrThrow(authMemberId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    fun logOut(member: Member, accessToken: String) {
        member.signOut()
        memberRepository.save(member)

        refreshTokenRepository.deleteByMemberId(member.id)
        blackListTokenCacheStorage.save(member.id, accessToken)
    }
}
