package com.petqua.application.auth

import com.petqua.domain.auth.oauth.OauthClientProvider
import com.petqua.domain.auth.oauth.OauthIdInfo
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.OauthUserInfo
import org.springframework.stereotype.Service
import java.net.URI

@Service
class OauthService(
    private val oauthClientProvider: OauthClientProvider,
) {

    fun getAuthCodeRequestUrl(oauthServerType: OauthServerType): URI {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.getAuthCodeRequestUrl()
    }

    fun requestOauthTokenInfo(oauthServerType: OauthServerType, code: String): OauthTokenInfo {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.requestToken(code)
    }

    fun requestOauthUserInfo(oauthServerType: OauthServerType, oauthAccessToken: String): OauthUserInfo {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.requestOauthUserInfo(oauthAccessToken)
    }

    fun updateOauthToken(oauthServerType: OauthServerType, oauthRefreshToken: String): OauthTokenInfo {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.updateToken(oauthRefreshToken)
    }

    fun disconnectBy(oauthServerType: OauthServerType, oauthAccessToken: String): OauthIdInfo {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        return oauthClient.disconnect(oauthAccessToken)
    }
}
