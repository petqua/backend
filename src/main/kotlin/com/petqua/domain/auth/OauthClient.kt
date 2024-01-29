package com.petqua.domain.auth

import java.net.URI

interface OauthClient {

    fun getAuthCodeRequestUrl(): URI

    fun oauthServerType(): OauthServerType

    fun requestOauthUserInfo(oauthTokenInfo: OauthTokenInfo): OauthUserInfo

    fun requestToken(code: String): OauthTokenInfo
}
