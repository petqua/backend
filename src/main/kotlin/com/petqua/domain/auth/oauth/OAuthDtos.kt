package com.petqua.domain.auth.oauth

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OauthTokenInfo(
    val accessToken: String
)

data class OauthUserInfo(
    val nickname: String,
    val imageUrl: String,
    val oauthId: String,
)