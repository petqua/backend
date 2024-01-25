package com.petqua.domain.auth.kakao

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth.client.kakao")
data class KakaoOauthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
