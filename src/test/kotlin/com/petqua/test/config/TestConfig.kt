package com.petqua.test.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer

@Configuration
class TestConfig : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf

    override fun extensions() = listOf(SpringExtension)

    companion object {
        @JvmStatic
        val container = GenericContainer<Nothing>("redis").apply {
            withExposedPorts(6379)
            start()
        }

        init {
            System.setProperty("spring.data.redis.host", container.host)
            System.setProperty("spring.data.redis.port", container.getMappedPort(6379).toString())
        }
    }
}
