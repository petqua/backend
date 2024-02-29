package com.petqua.domain.auth.oauth

import com.petqua.exception.auth.OauthClientException
import com.petqua.exception.auth.OauthClientExceptionType.UNSUPPORTED_OAUTH_SERVER_TYPE
import java.util.Locale.ENGLISH

enum class OauthServerType(
    val number: Int
) {
    KAKAO(1)
    ;

    companion object {
        fun from(name: String): OauthServerType {
            return enumValues<OauthServerType>().find { it.name == name.uppercase(ENGLISH) }
                ?: throw OauthClientException(UNSUPPORTED_OAUTH_SERVER_TYPE)
        }

        fun numberOf(serverNumber: Int): OauthServerType {
            return enumValues<OauthServerType>().find { it.number == serverNumber }
                ?: throw OauthClientException(UNSUPPORTED_OAUTH_SERVER_TYPE)
        }
    }
}
