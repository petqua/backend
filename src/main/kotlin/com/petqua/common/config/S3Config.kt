package com.petqua.common.config

import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper
import com.amazonaws.regions.Regions.AP_NORTHEAST_2
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class S3Config {

    @Bean
    fun amazonS3(): AmazonS3 {
        return AmazonS3ClientBuilder.standard()
            .withRegion(AP_NORTHEAST_2)
            .withCredentials(EC2ContainerCredentialsProviderWrapper())
            .build()
    }
}
