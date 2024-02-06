package com.petqua.test

import com.petqua.presentation.auth.AuthExtractor
import com.petqua.presentation.auth.AuthResponse
import com.petqua.test.config.OauthTestConfig
import io.kotest.core.spec.style.BehaviorSpec
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import

@Import(OauthTestConfig::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
abstract class ApiTestConfig() : BehaviorSpec() {

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    private lateinit var dataCleaner: DataCleaner

    @Autowired
    protected lateinit var authExtractor: AuthExtractor


    init {
        this.beforeTest {
            RestAssured.port = this.port
        }

        afterContainer {
            dataCleaner.clean()
        }
    }

    final fun getMemberIdByAccessToken(accessToken: String): Long {
        return authExtractor.getAccessTokenClaimsOrThrow(accessToken).memberId
    }

    final fun signInAsMember(): AuthResponse {
        val response = requestSignIn()
        return response.`as`(AuthResponse::class.java)
    }

    private fun requestSignIn(): Response {
        return Given {
            log().all()
                .queryParam("code", "code")
        } When {
            get("/auth/login/{oauthServerType}", "kakao")
        } Then {
            log().all()
        } Extract {
            response()
        }
    }
}
