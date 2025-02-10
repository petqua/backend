package com.petqua.application.auth

import com.petqua.common.domain.findActiveByIdOrThrow
import com.petqua.domain.auth.AuthCredentials
import com.petqua.domain.auth.AuthCredentialsRepository
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.BlackListTokenCacheStorage
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.auth.token.findByTokenOrThrow
import com.petqua.domain.cart.CartProductRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AuthService(
    private val authCredentialsRepository: AuthCredentialsRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val cartProductRepository: CartProductRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val blackListTokenCacheStorage: BlackListTokenCacheStorage,
) {

    fun findOrCreateAuthCredentialsBy(
        oauthServerType: OauthServerType,
        oauthId: Long,
    ): AuthCredentials {
        return authCredentialsRepository.findByOauthIdAndOauthServerNumberAndIsDeletedFalse(
            oauthId = oauthId,
            oauthServerNumber = oauthServerType.number
        ) ?: authCredentialsRepository.save(
            AuthCredentials.of(
                oauthId = oauthId,
                oauthServerNumber = oauthServerType.number,
            )
        )
    }

    fun validateTokenExpiredStatusForExtendLogin(accessToken: String, refreshToken: String) {
        authTokenProvider.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
    }

    @Transactional(readOnly = true)
    fun findAuthCredentialsBy(refreshToken: String): AuthCredentials {
        val savedRefreshToken = refreshTokenRepository.findByTokenOrThrow(refreshToken) {
            AuthException(INVALID_REFRESH_TOKEN)
        }
        return authCredentialsRepository.findActiveByIdOrThrow(savedRefreshToken.memberId) {
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
    fun findAuthCredentialsBy(authCredentialsId: Long): AuthCredentials {
        return authCredentialsRepository.findActiveByIdOrThrow(authCredentialsId) {
            MemberException(NOT_FOUND_MEMBER)
        }
    }

    fun updateOauthToken(authCredentials: AuthCredentials, oauthTokenInfo: OauthTokenInfo) {
        authCredentials.updateOauthToken(
            accessToken = oauthTokenInfo.accessToken,
            expiresIn = oauthTokenInfo.expiresIn,
            refreshToken = oauthTokenInfo.refreshToken
        )
        authCredentialsRepository.save(authCredentials)
    }

    fun delete(member: Member, authCredentials: AuthCredentials) {
        member.delete()
        memberRepository.save(member)
        authCredentials.delete()
        authCredentialsRepository.save(authCredentials)

        cartProductRepository.deleteByMemberId(member.id)
        refreshTokenRepository.deleteByMemberId(member.id)
    }

    fun logOut(authCredentials: AuthCredentials, accessToken: String) {
        authCredentials.signOut()
        authCredentialsRepository.save(authCredentials)

        refreshTokenRepository.deleteByMemberId(authCredentials.id)
        blackListTokenCacheStorage.save(authCredentials.id, accessToken)
    }
}
