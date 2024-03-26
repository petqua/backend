package com.petqua.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY
import io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val ACCESS_TOKEN_SECURITY_SCHEME_KEY = "AccessToken(Bearer)"
const val SIGN_UP_TOKEN_SECURITY_SCHEME_KEY = "Sign-Up-Authorization"

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .addServersItem(server())
            .components(authComponents())
            .info(info())
    }

    private fun server(): Server {
        return Server().url("/")
    }

    private fun authComponents(): Components {
        return Components()
            .addSecuritySchemes(ACCESS_TOKEN_SECURITY_SCHEME_KEY, accessTokenSecurityScheme())
            .addSecuritySchemes(SIGN_UP_TOKEN_SECURITY_SCHEME_KEY, signUpTokenSecurityScheme())
    }

    private fun accessTokenSecurityScheme(): SecurityScheme {
        return SecurityScheme()
            .name(ACCESS_TOKEN_SECURITY_SCHEME_KEY)
            .type(HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("https://api.petqua.co.kr/auth/Kakao 로그인 후 발급받은 토큰을 입력하세요")
    }

    private fun signUpTokenSecurityScheme(): SecurityScheme {
        return SecurityScheme()
            .name(SIGN_UP_TOKEN_SECURITY_SCHEME_KEY)
            .type(APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("https://api.petqua.co.kr/auth/Kakao 최초 로그인 후 발급받은 회원가입 전용 토큰을 입력하세요")
    }

    private fun info(): Info {
        return Info()
            .title("펫쿠아 서버 API 명세서")
            .description("Swagger UI 에서 테스트를 해봅시다")
            .version("1.0.0")
    }
}
