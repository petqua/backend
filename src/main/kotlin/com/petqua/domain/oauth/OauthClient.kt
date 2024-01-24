package com.petqua.domain.oauth

interface OauthClient {

    fun oauthServerType(): OauthServerType

    fun requestOauthUserInfo(oauthTokenInfo: OauthTokenInfo): OauthUserInfo

    fun requestToken(code: String): OauthTokenInfo
}
