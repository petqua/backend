package com.petqua.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",
                "https://petqua.co.kr",
                "http://localhost:5173",
                "http://localhost:4173",
            )
            .allowedOriginPatterns("https://frontend-git-*-api-blueapple99s-projects.vercel.app")
            .allowedMethods(
                OPTIONS.name(),
                GET.name(),
                POST.name(),
                PUT.name(),
                PATCH.name(),
                DELETE.name(),
            )
            .allowCredentials(true)
            .exposedHeaders("*")
    }
}
