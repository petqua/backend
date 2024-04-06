package com.petqua.application.auth

import com.petqua.domain.auth.AuthMember
import com.petqua.domain.auth.oauth.OauthServerType
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

        val authMember = authService.findOrCreateAuthMemberBy(oauthServerType, oauthUserInfo)
        authService.updateOauthToken(authMember, oauthTokenInfo)
        val member = authService.findOrCreateMemberBy(authMember)

        return authService.createAuthToken(member)
    }

    fun extendLogin(accessToken: String, refreshToken: String): AuthTokenInfo {
        authService.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
        val authMember = authService.findAuthMemberBy(accessToken = accessToken, refreshToken = refreshToken)
        updateOauthTokenIfExpired(authMember)
        val member = authService.findMemberByAuthMemberId(authMember.id)
        return authService.createAuthToken(member)
    }

    private fun updateOauthTokenIfExpired(authMember: AuthMember) {
        if (authMember.hasExpiredOauthToken()) {
            val oauthTokenInfo = oauthService.updateOauthToken(
                oauthServerType = authMember.oauthServerType,
                oauthRefreshToken = authMember.oauthRefreshToken
            )
            authService.updateOauthToken(authMember, oauthTokenInfo)
        }
    }

    fun deleteBy(memberId: Long) {
        val member = authService.findMemberBy(memberId)
        val authMember = authService.findAuthMemberBy(member.authMemberId)
        updateOauthTokenIfExpired(authMember)
        oauthService.disconnectBy(
            oauthServerType = authMember.oauthServerType,
            oauthAccessToken = authMember.oauthAccessToken
        )
        authService.delete(member, authMember)
    }

    fun logOut(accessToken: String, refreshToken: String) {
        val member = authService.findMemberBy(refreshToken = refreshToken)
        authService.logOut(member, accessToken)
    }
}
