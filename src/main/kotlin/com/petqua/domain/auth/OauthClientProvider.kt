package com.petqua.domain.auth

import com.petqua.common.exception.auth.OauthClientException
import com.petqua.common.exception.auth.OauthClientExceptionType.UNSUPPORTED_OAUTH_SERVER_TYPE
import org.springframework.stereotype.Component

@Component
class OauthClientProvider(
    private val oauthClients: Set<OauthClient>
) {

    fun getOauthClient(oauthServerType: OauthServerType): OauthClient {
        return oauthClients.find { it.oauthServerType() == oauthServerType }
            ?: throw OauthClientException(UNSUPPORTED_OAUTH_SERVER_TYPE)
    }
}
