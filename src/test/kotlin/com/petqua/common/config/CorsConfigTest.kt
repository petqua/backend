package com.petqua.common.config

import com.petqua.test.ApiTestConfig
import io.kotest.matchers.shouldBe
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN
import org.springframework.http.HttpHeaders.ORIGIN

class CorsConfigTest : ApiTestConfig() {

    init {
        Given("서버에서 허용된 origin에서 오는 요청을 처리할 때") {
            val originUrl = "https://frontend-git-67-api-blueapple99s-projects.vercel.app/"

            When("vercel 관련 origin에서 요청이 오면") {
                val response = Given {
                    log().all()
                    header(ORIGIN, originUrl)
                } When {
                    get("/")
                } Then {
                    log().all()
                } Extract {
                    response()
                }
                Then("접근 제어를 허용한다") {
                    response.header(ACCESS_CONTROL_ALLOW_ORIGIN) shouldBe originUrl
                }
            }
        }
    }
}
