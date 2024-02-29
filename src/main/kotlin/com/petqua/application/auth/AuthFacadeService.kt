package com.petqua.application.auth

import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.member.Member
import org.springframework.stereotype.Service
import java.net.URI

@Service
class AuthFacadeService(
    private val authService: AuthService,
    private val oauthService: OauthService,
) {

    fun getAuthCodeRequestUrl(oauthServerType: OauthServerType): URI {
        return oauthService.getAuthCodeRequestUrl(oauthServerType)
    }

    fun login(oauthServerType: OauthServerType, code: String): AuthTokenInfo {
        val oauthTokenInfo = oauthService.requestOauthTokenInfo(oauthServerType, code)
        val oauthUserInfo = oauthService.requestOauthUserInfo(oauthServerType, oauthTokenInfo.accessToken)
        val member = authService.findOrCreateMemberBy(oauthServerType, oauthTokenInfo, oauthUserInfo)
        return authService.createAuthToken(member)
    }

    fun extendLogin(accessToken: String, refreshToken: String): AuthTokenInfo {
        val member = authService.findMemberBy(accessToken = accessToken, refreshToken = refreshToken)
        updateOauthTokenIfExpired(member)
        return authService.createAuthToken(member)
    }

    private fun updateOauthTokenIfExpired(member: Member) {
        if (member.hasExpiredOauthToken()) {
            val oauthTokenInfo = oauthService.updateOauthToken(
                oauthServerType = member.oauthServerType,
                oauthRefreshToken = member.oauthRefreshToken
            )
            authService.updateOauthToken(member, oauthTokenInfo)
        }
    }

    fun deleteBy(memberId: Long) {
        val member = authService.findMemberBy(memberId)
        updateOauthTokenIfExpired(member)
        oauthService.disconnectBy(
            oauthServerType = member.oauthServerType,
            oauthAccessToken = member.oauthAccessToken
        )
        authService.delete(member)
    }
}
