package com.petqua.common.config

import ch.qos.logback.access.tomcat.LogbackValve
import org.apache.catalina.Context
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader


@Configuration
class AccessLogConfig(
    private val resourceLoader: ResourceLoader
) {

    @Bean
    fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<*> {
        return WebServerFactoryCustomizer { container: WebServerFactory? ->
            if (container is TomcatServletWebServerFactory) {
                container.addContextCustomizers(TomcatContextCustomizer { context: Context ->
                    val valve = LogbackValve()
                    valve.filename = resourceLoader
                        .getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "logback-access.xml")
                        .filename
                    context.pipeline.addValve(valve)
                })
            }
        }
    }
}
