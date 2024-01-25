package com.petqua.domain.auth

import com.petqua.domain.auth.kakao.KakaoOauthApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class OauthApiClientConfig {

    @Bean
    fun KakaoOauthApiClient(): KakaoOauthApiClient {
        return HttpServiceProxyFactory.builderFor(
            WebClientAdapter.create(WebClient.create())
        ).build().createClient(KakaoOauthApiClient::class.java)
    }
}
