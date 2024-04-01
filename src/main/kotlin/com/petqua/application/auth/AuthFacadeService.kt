package com.petqua.application.auth

import com.petqua.application.token.AuthTokenInfo
import com.petqua.application.token.TokenService
import com.petqua.domain.auth.AuthCredentials
import com.petqua.domain.auth.oauth.OauthServerType
import org.springframework.stereotype.Service
import java.net.URI

@Service
class AuthFacadeService(
    private val authService: AuthService,
    private val oauthService: OauthService,
    private val tokenService: TokenService,
) {

    fun getAuthCodeRequestUrl(oauthServerType: OauthServerType): URI {
        return oauthService.getAuthCodeRequestUrl(oauthServerType)
    }

    fun login(oauthServerType: OauthServerType, code: String): AuthTokenInfo {
        val oauthTokenInfo = oauthService.requestOauthTokenInfo(oauthServerType, code)
        val oauthUserInfo = oauthService.requestOauthUserInfo(oauthServerType, oauthTokenInfo.accessToken)

        val authMember = authService.findOrCreateAuthMemberBy(oauthServerType, oauthUserInfo.oauthId)
        authService.updateOauthToken(authMember, oauthTokenInfo)

        return tokenService.createAuthOrSignUpToken(authMember.id)
    }

    fun extendLogin(accessToken: String, refreshToken: String): AuthTokenInfo {
        authService.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
        val authMember = authService.findAuthMemberBy(accessToken = accessToken, refreshToken = refreshToken)
        updateOauthTokenIfExpired(authMember)

        return tokenService.createAuthOrSignUpToken(authMember.id)
    }

    private fun updateOauthTokenIfExpired(authCredentials: AuthCredentials) {
        if (authCredentials.hasExpiredOauthToken()) {
            val oauthTokenInfo = oauthService.updateOauthToken(
                oauthServerType = authCredentials.oauthServerType,
                oauthRefreshToken = authCredentials.oauthRefreshToken
            )
            authService.updateOauthToken(authCredentials, oauthTokenInfo)
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
