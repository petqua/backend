package com.petqua.test.fake

import com.petqua.domain.auth.oauth.OauthIdInfo
import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.kakao.KakaoAccount
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoUserInfo
import com.petqua.domain.auth.oauth.kakao.Profile
import kotlin.random.Random

class FakeKakaoOauthApiClient() : KakaoOauthApiClient {

    override fun fetchToken(body: Map<String, String>): OauthTokenInfo {
        return OauthTokenInfo(
            tokenType = "bearer",
            accessToken = "oauthAccessToken",
            expiresIn = 21599,
            refreshToken = "oauthRefreshToken",
            refreshTokenExpiresIn = 5183999,
        )
    }

    override fun fetchUserInfo(bearerToken: String): KakaoUserInfo {
        val kakaoAccount = KakaoAccount(
            Profile(
                nickname = "nickname",
            )
        )

        return KakaoUserInfo(
            kakaoAccount = kakaoAccount,
            oauthId = Random.nextLong()
        )
    }

    override fun disconnect(accessToken: String): OauthIdInfo {
        return OauthIdInfo(Random.nextLong())
    }
}
