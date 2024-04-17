package com.petqua.test.config

import com.amazonaws.services.s3.AmazonS3
import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.test.fake.FakeAmazonS3
import com.petqua.test.fake.FakeKakaoOauthApiClient
import com.petqua.test.fake.FakeTossPaymentsApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class ApiClientTestConfig {

    @Bean
    fun kakaoOauthApiClient(): KakaoOauthApiClient {
        return FakeKakaoOauthApiClient()
    }

    @Bean
    fun tossPaymentsApiClient(): TossPaymentsApiClient {
        return FakeTossPaymentsApiClient()
    }

    @Bean
    fun amazonS3(): AmazonS3 {
        return FakeAmazonS3()
    }
}
