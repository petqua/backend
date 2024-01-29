package com.petqua.common.config

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@EnableCaching
@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = ConcurrentMapCacheManager()
        cacheManager.setCacheNames(listOf("banners", "announcements"))
        return cacheManager
    }
}
