package com.petqua.presentation.auth

import com.petqua.test.ApiTestConfig
import com.petqua.test.config.OauthTestConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.OK

@Import(OauthTestConfig::class)
class OauthControllerTest : ApiTestConfig() {

    init {
        Given("소셜 로그인을 할 때") {

            When("카카오 로그인을 시도하면") {
                val response = Given {
                    log().all()
                } When {
                    queryParam("code", "accessCode")
                    get("/oauth/login/kakao")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("인증토큰이 반환된다.") {
                    val authTokenInfo = response.`as`(AuthResponse::class.java)

                    response.statusCode shouldBe OK.value()
                    authTokenInfo.accessToken shouldNotBe null
                }
            }
        }
    }
}
