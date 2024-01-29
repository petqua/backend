package com.petqua.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .components(Components())
            .info(info())
    }

    private fun info(): Info {
        return Info()
            .title("펫쿠아 서버 API 명세서")
            .description("Swagger UI 에서 테스트를 해봅시다")
            .version("1.0.0")
    }
}
