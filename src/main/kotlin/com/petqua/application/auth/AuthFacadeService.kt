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

        val authCredentials = authService.findOrCreateAuthCredentialsBy(oauthServerType, oauthUserInfo.oauthId)
        authService.updateOauthToken(authCredentials, oauthTokenInfo)

        return tokenService.createAuthOrSignUpToken(authCredentials.id)
    }

    fun extendLogin(accessToken: String, refreshToken: String): AuthTokenInfo {
        authService.validateTokenExpiredStatusForExtendLogin(accessToken, refreshToken)
        val authCredentials = authService.findAuthCredentialsBy(refreshToken = refreshToken)
        updateOauthTokenIfExpired(authCredentials)

        return tokenService.createAuthOrSignUpToken(authCredentials.id)
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
        val authCredentials = authService.findAuthCredentialsBy(member.authCredentialsId)
        updateOauthTokenIfExpired(authCredentials)
        oauthService.disconnectBy(
            oauthServerType = authCredentials.oauthServerType,
            oauthAccessToken = authCredentials.oauthAccessToken
        )
        authService.delete(member, authCredentials)
    }

    fun logOut(accessToken: String, refreshToken: String) {
        val authCredentials = authService.findAuthCredentialsBy(refreshToken = refreshToken)
        authService.logOut(authCredentials, accessToken)
    }
}
