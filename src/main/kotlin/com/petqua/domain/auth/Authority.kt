package com.petqua.domain.auth

import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType
import java.util.Locale

enum class Authority {
    GUEST, MEMBER, SELLER, ADMIN
    ;

    companion object {
        fun from(name: String): Authority {
            return enumValues<Authority>().find {it.name == name.uppercase(Locale.ENGLISH)}
                ?: throw AuthException(AuthExceptionType.UNSUPPORTED_AUTHORITY)
        }
    }
}
