package com.petqua.domain.auth.oauth

import java.net.URI

interface OauthClient {

    fun getAuthCodeRequestUrl(): URI

    fun oauthServerType(): OauthServerType

    fun requestOauthUserInfo(oauthTokenInfo: OauthTokenInfo): OauthUserInfo

    fun requestToken(code: String): OauthTokenInfo

    fun updateToken(refreshToken: String): OauthTokenInfo

    fun disconnect(accessToken: String)
}
