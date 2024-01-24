package com.petqua.domain.oauth

import com.petqua.common.exception.oauth.OauthClientException
import org.springframework.stereotype.Component

@Component
class OauthClientProvider(
    private val oauthClients: Set<OauthClient>
) {

    fun getOauthClient(oauthServerType: OauthServerType): OauthClient {
        return oauthClients.find { it.oauthServerType() == oauthServerType }
            ?: throw OauthClientException()
    }
}
