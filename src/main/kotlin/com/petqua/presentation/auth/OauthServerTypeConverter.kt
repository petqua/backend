package com.petqua.presentation.auth

import com.petqua.domain.auth.oauth.OauthServerType
import org.springframework.core.convert.converter.Converter

class OauthServerTypeConverter : Converter<String, OauthServerType> {

    override fun convert(source: String): OauthServerType {
        return OauthServerType.from(source)
    }
}
