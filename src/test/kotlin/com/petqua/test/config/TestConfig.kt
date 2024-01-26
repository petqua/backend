package com.petqua.test.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringExtension
import org.springframework.context.annotation.Configuration

@Configuration
class TestConfig : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf

    override fun extensions() = listOf(SpringExtension)
}
