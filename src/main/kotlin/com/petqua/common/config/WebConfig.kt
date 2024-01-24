package com.petqua.common.config

import com.petqua.presentation.OauthServerTypeConverter
import org.springframework.context.annotation.Configuration

import org.springframework.format.FormatterRegistry

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(OauthServerTypeConverter())
    }
}
