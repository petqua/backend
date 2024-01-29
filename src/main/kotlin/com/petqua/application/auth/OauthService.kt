package com.petqua.application.auth

import com.petqua.domain.auth.Authority.MEMBER
import com.petqua.domain.auth.oauth.OauthClientProvider
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthUserInfo
import com.petqua.domain.auth.token.AuthToken
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URI
import java.util.Date

@Transactional
@Service
class OauthService(
    private val oauthClientProvider: OauthClientProvider,
    private val memberRepository: MemberRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    fun getAuthCodeRequestUrl(oauthServerType: OauthServerType): URI {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.getAuthCodeRequestUrl()
    }

    fun login(oauthServerType: OauthServerType, code: String): AuthResponse {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        val oauthUserInfo = oauthClient.requestOauthUserInfo(oauthClient.requestToken(code))
        val member = getMemberByOauthInfo(oauthUserInfo.oauthId, oauthServerType)
            ?: createMember(oauthUserInfo, oauthServerType)
        val authToken = createAuthToken(member)
        return AuthResponse(
            accessToken = authToken.accessToken,
            refreshToken = authToken.refreshToken,
        )
    }

    private fun getMemberByOauthInfo(oauthId: String, oauthServerType: OauthServerType): Member? {
        return memberRepository.findByOauthIdAndOauthServerNumber(oauthId, oauthServerType.number)
    }

    private fun createMember(oauthUserInfo: OauthUserInfo, oauthServerType: OauthServerType): Member {
        return memberRepository.save(
            Member(
                oauthId = oauthUserInfo.oauthId,
                oauthServerNumber = oauthServerType.number,
                authority = MEMBER
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
}
