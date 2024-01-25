package com.petqua.domain.auth.kakao

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.petqua.domain.auth.OauthUserInfo

@JsonNaming(SnakeCaseStrategy::class)
class KakaoUserInfo(
    private val kakaoAccount: KakaoAccount,

    @JsonProperty("id")
    private val oauthId: String,
) {

    fun toOauthUserInfo(): OauthUserInfo {
        return OauthUserInfo(
            nickname = kakaoAccount.profile.nickname,
            imageUrl = kakaoAccount.profile.imageUrl,
            oauthId = oauthId
        )
    }
}

data class KakaoAccount(
    val profile: Profile
)

data class Profile(
    val nickname: String,

    @JsonProperty("profile_image_url")
    val imageUrl: String
)
