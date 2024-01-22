package com.petqua

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PetquaApplication

fun main(args: Array<String>) {
    runApplication<PetquaApplication>(*args)
}
