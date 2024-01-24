package com.petqua.application.oauth

import com.petqua.domain.oauth.OauthClientProvider
import com.petqua.domain.oauth.OauthServerType
import com.petqua.presentation.OauthResponse
import org.springframework.stereotype.Service

@Service
class OauthService(
    private val oauthClientProvider: OauthClientProvider,
) {

    fun login(oauthServerType: OauthServerType, code: String): OauthResponse {
        val oauthClient = oauthClientProvider.getOauthClient(oauthServerType)
        val oauthUserInfo = oauthClient.requestOauthUserInfo(oauthClient.requestToken(code))

        TODO("oauthUserInfo를 통해 멤버 조회 후 토큰(JWT) 반환")
    }
}
