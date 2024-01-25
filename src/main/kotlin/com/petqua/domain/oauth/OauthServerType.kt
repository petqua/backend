package com.petqua.domain.oauth

import com.petqua.common.exception.oauth.OauthClientException
import com.petqua.common.exception.oauth.OauthClientExceptionType.UNSUPPORTED_OAUTH_SERVER_TYPE
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
    }
}
