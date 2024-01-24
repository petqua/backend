package com.petqua.domain.oauth

interface OauthClient {

    fun oauthServerType() : OauthServerType

    fun requestOauthUserInfo(
        code: String
    ) : OauthUserInfo
}
