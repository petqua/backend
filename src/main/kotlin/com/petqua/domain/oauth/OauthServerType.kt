package com.petqua.domain.oauth

import java.util.Locale.ENGLISH

enum class OauthServerType {
    KAKAO;

    companion object {
        fun from(name: String): OauthServerType {
            return OauthServerType.valueOf(name.uppercase(ENGLISH))
        }
    }
}
