package com.petqua.domain.oauth

import org.springframework.stereotype.Component

@Component
class OauthClientProvider(oauthClients: Set<OauthClient>) {

    private val clientMapper = oauthClients.associateBy(OauthClient::oauthServerType)

    fun getOauthClient(oauthServerType: OauthServerType): OauthClient {
        return clientMapper[oauthServerType]!! // TODO 커스텀 예외 생성
    }
}
