package com.petqua

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
@ConfigurationPropertiesScan
class PetquaApplication

fun main(args: Array<String>) {
    runApplication<PetquaApplication>(*args)
}
