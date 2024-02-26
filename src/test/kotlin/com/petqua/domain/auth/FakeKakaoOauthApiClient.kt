package com.petqua.domain.auth

import com.petqua.domain.auth.oauth.OauthTokenInfo
import com.petqua.domain.auth.oauth.kakao.KakaoAccount
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoUserInfo
import com.petqua.domain.auth.oauth.kakao.Profile
import java.util.*

class FakeKakaoOauthApiClient : KakaoOauthApiClient {
    override fun fetchToken(body: Map<String, String>): OauthTokenInfo {
        return OauthTokenInfo(
            tokenType = "bearer",
            accessToken = "accessToken",
            expiresIn = 21599,
            refreshToken = "refreshToken",
            refreshTokenExpiresIn = 5183999,
        )
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

    override fun disconnect(accessToken: String): Long {
        TODO("Not yet implemented")
    }
}
