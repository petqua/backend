package com.petqua.presentation.auth

import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.config.OauthTestConfig
import com.petqua.test.fixture.member
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus.OK
import java.util.Date

@Import(OauthTestConfig::class)
class AuthControllerTest(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,
) : ApiTestConfig() {

    init {
        Given("소셜 로그인을 할 때") {

            When("카카오 로그인을 시도하면") {
                val response = Given {
                    log().all()
                } When {
                    queryParam("code", "accessCode")
                    get("/auth/login/kakao")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("인증토큰이 반환된다.") {
                    val authResponse = response.`as`(AuthResponse::class.java)

                    response.statusCode shouldBe OK.value()
                    authResponse.accessToken.shouldNotBeNull()
                }
            }
        }

        Given("로그인 연장을") {
            val member = memberRepository.save(member())
            val expiredAccessToken = authTokenProvider.createAuthToken(member, Date(0)).accessToken
            val refreshToken = authTokenProvider.createAuthToken(member, Date()).refreshToken
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = refreshToken
                )
            )

            When("요청하면") {
                val response = Given {
                    log().all()
                    auth().preemptive().oauth2(expiredAccessToken)
                    cookie("refresh-token", refreshToken)
                } When {
                    get("/auth/token")
                } Then {
                    log().all()
                } Extract {
                    response()
                }

                Then("인증토큰이 반환된다.") {
                    val authResponse = response.`as`(AuthResponse::class.java)

                    response.statusCode shouldBe OK.value()
                    authResponse.accessToken.shouldNotBeNull()
                }
            }
        }
    }
}
