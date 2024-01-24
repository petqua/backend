package com.petqua.domain.oauth

import com.petqua.common.exception.oauth.OauthClientException
import java.util.Locale.ENGLISH

enum class OauthServerType {
    KAKAO;

    companion object {
        fun from(name: String): OauthServerType {
            return enumValues<OauthServerType>().find { it.name == name.uppercase(ENGLISH) }
                ?: throw OauthClientException()
        }
    }
}
