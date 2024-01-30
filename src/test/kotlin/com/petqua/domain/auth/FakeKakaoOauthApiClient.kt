package com.petqua.domain.auth

import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.kakao.KakaoAccount
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoUserInfo
import com.petqua.domain.auth.oauth.kakao.Profile
import java.util.UUID
import org.springframework.util.MultiValueMap

class FakeKakaoOauthApiClient : KakaoOauthApiClient {
    override fun fetchToken(body: MultiValueMap<String, String>): OauthTokenInfo {
        return OauthTokenInfo("accessToken")
    }

    override fun fetchUserInfo(bearerToken: String): KakaoUserInfo {
        val kakaoAccount = KakaoAccount(
            Profile(
                nickname = "nickname",
                imageUrl = "imageUrl"
            )
        )
        return KakaoUserInfo(
            kakaoAccount = kakaoAccount,
            oauthId = "oauthId" + UUID.randomUUID().toString()
        )
    }
}
