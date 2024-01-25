package com.petqua.domain.auth

import com.petqua.common.exception.auth.OauthClientException
import com.petqua.common.exception.auth.OauthClientExceptionType.UNSUPPORTED_OAUTH_SERVER_TYPE
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
