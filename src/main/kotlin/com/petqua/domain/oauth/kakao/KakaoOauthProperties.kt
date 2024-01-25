package com.petqua.domain.oauth.kakao

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oauth.client.kakao")
data class KakaoOauthProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
