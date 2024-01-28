package com.petqua.domain.oauth.kakao

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.petqua.domain.oauth.OauthUserInfo

@JsonNaming(SnakeCaseStrategy::class)
class KakaoUserInfo(
    private val kakaoAccount: KakaoAccount,

    @JsonProperty("id")
    private val oauthId: String,
) : OauthUserInfo {

    override fun nickname(): String {
        return kakaoAccount.profile.nickname
    }

    override fun imageUrl(): String {
        return kakaoAccount.profile.imageUrl
    }

    override fun oauthId(): String {
        return oauthId
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
