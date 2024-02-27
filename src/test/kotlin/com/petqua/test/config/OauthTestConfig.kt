package com.petqua.test.config

import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.test.fake.FakeKakaoOauthApiClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class OauthTestConfig {

    @Bean
    fun kakaoOauthApiClient(): KakaoOauthApiClient {
        return FakeKakaoOauthApiClient()
    }
}
