package com.petqua.test.config

import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.domain.auth.FakeKakaoOauthApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.test.fake.FakeTossPaymentsApiClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class ApiClientTestConfig {

    @Bean
    fun kakaoOauthApiClient(): KakaoOauthApiClient {
        return FakeKakaoOauthApiClient()
    }

    @Bean
    fun tossPaymentsApiClient(): TossPaymentsApiClient {
        return FakeTossPaymentsApiClient()
    }
}
