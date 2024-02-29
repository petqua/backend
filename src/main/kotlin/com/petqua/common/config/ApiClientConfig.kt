package com.petqua.common.config

import com.petqua.application.payment.infra.TossPaymentsApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@Profile("!test")
class ApiClientConfig {

    @Bean
    fun tossPaymentsApiClient(): TossPaymentsApiClient {
        return createApiClient(TossPaymentsApiClient::class.java)
    }

    private fun <T> createApiClient(clazz: Class<T>): T {
        val webClient = WebClient.create()
        val builder = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
        return builder.build().createClient(clazz)
    }
}
