package com.petqua.test

import io.kotest.core.spec.style.BehaviorSpec
import io.restassured.RestAssured
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = RANDOM_PORT)
abstract class ApiTestConfig : BehaviorSpec() {

    @LocalServerPort
    protected val port: Int = RestAssured.port

    @Autowired
    private lateinit var dataCleaner: DataCleaner

    init {
        afterContainer {
            dataCleaner.clean()
        }
    }
}