package com.petqua.common.config

import ch.qos.logback.access.tomcat.LogbackValve
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AccessLogConfig {

    @Bean
    fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer<TomcatServletWebServerFactory> { factory ->
            val logbackValve = LogbackValve()
            logbackValve.filename = "logback-access.xml"
            factory.addContextValves(logbackValve)
        }
    }
}
