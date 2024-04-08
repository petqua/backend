package com.petqua.domain.auth.oauth

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OauthTokenInfo(
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String?,
    val refreshTokenExpiresIn: Long?,
)

data class OauthUserInfo(
    val nickname: String,
    val oauthId: Long,
)

data class OauthIdInfo(
    val id: Long,
)
