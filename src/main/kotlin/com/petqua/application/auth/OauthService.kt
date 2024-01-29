package com.petqua.application.auth

import com.petqua.domain.auth.OauthClientProvider
import com.petqua.domain.auth.OauthServerType
import com.petqua.domain.auth.OauthUserInfo
import com.petqua.domain.auth.token.AuthToken
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.presentation.OauthResponse
import org.springframework.stereotype.Service
import java.net.URI
import java.util.*

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

    fun login(oauthServerType: OauthServerType, code: String): OauthResponse {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        val oauthUserInfo = oauthClient.requestOauthUserInfo(oauthClient.requestToken(code))
        val member = getMemberByOauthInfo(oauthUserInfo.oauthId, oauthServerType)
            ?: createMember(oauthUserInfo, oauthServerType)
        val authToken = createAuthToken(member)
        return OauthResponse(
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
                oauthServerNumber = oauthServerType.number
            )
        )
    }

    private fun createAuthToken(member: Member): AuthToken {
        val authToken = authTokenProvider.createAuthToken(member.id, Date())
        refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = authToken.refreshToken
            )
        )
        return authToken
    }
}
