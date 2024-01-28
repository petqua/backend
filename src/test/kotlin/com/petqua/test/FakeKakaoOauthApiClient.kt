package com.petqua.test

import com.petqua.domain.auth.OauthTokenInfo
import com.petqua.domain.auth.kakao.KakaoAccount
import com.petqua.domain.auth.kakao.KakaoOauthApiClient
import com.petqua.domain.auth.kakao.KakaoUserInfo
import com.petqua.domain.auth.kakao.Profile
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
            oauthId = "oauthId"
        )
    }
}
