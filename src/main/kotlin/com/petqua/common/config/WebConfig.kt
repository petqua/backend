package com.petqua.common.config

import com.petqua.presentation.auth.LoginArgumentResolver
import com.petqua.presentation.auth.OauthServerTypeConverter
import com.petqua.presentation.auth.OptionalLoginArgumentResolver
import com.petqua.presentation.auth.TokenArgumentResolver
import org.springframework.context.annotation.Configuration

import org.springframework.format.FormatterRegistry
import org.springframework.web.method.support.HandlerMethodArgumentResolver

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val loginArgumentResolver: LoginArgumentResolver,
    private val tokenArgumentResolver: TokenArgumentResolver,
    private val optionalLoginArgumentResolver: OptionalLoginArgumentResolver,
) : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(OauthServerTypeConverter())
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver?>) {
        resolvers.addAll(
            listOf(
                loginArgumentResolver,
                tokenArgumentResolver,
                optionalLoginArgumentResolver,
            )
        )
    }
}
