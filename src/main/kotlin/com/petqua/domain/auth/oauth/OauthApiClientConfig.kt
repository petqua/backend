package com.petqua.domain.auth.oauth

import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@Profile("!test")
class OauthApiClientConfig {

    @Bean
    fun kakaoOauthApiClient(): KakaoOauthApiClient {
        return HttpServiceProxyFactory.builderFor(
            WebClientAdapter.create(WebClient.create())
        ).build().createClient(KakaoOauthApiClient::class.java)
    }
}
