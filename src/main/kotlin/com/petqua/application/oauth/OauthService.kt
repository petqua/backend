package com.petqua.application.oauth

import com.petqua.domain.member.Member
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.oauth.AuthTokenProvider
import com.petqua.domain.oauth.OauthClientProvider
import com.petqua.domain.oauth.OauthServerType
import com.petqua.domain.oauth.OauthUserInfo
import com.petqua.presentation.OauthResponse
import org.springframework.stereotype.Service

@Service
class OauthService(
    private val oauthClientProvider: OauthClientProvider,
    private val memberRepository: MemberRepository,
    private val authTokenProvider: AuthTokenProvider,
) {

    fun login(oauthServerType: OauthServerType, code: String): OauthResponse {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        val oauthUserInfo = oauthClient.requestOauthUserInfo(oauthClient.requestToken(code))
        val member = getMemberByOauthInfo(oauthUserInfo.oauthId, oauthServerType)
            ?: createMember(oauthUserInfo, oauthServerType)
        val authToken = authTokenProvider.createAuthToken(member.id)
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
}
