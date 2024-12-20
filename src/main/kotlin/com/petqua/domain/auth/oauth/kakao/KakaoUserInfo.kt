package com.petqua.domain.auth.oauth.kakao

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.petqua.domain.auth.oauth.OauthUserInfo

@JsonNaming(SnakeCaseStrategy::class)
class KakaoUserInfo(
    private val kakaoAccount: KakaoAccount,

    @JsonProperty("id")
    private val oauthId: Long,
) {

    fun toOauthUserInfo(): OauthUserInfo {
        return OauthUserInfo(
            nickname = kakaoAccount.profile.nickname,
            oauthId = oauthId
        )
    }
}

data class KakaoAccount(
    val profile: Profile,
)

data class Profile(
    val nickname: String,
)
