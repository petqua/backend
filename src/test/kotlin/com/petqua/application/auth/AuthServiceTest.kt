package com.petqua.application.auth

import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.test.DataCleaner
import com.petqua.test.config.OauthTestConfig
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Import
import java.util.Date

@SpringBootTest(webEnvironment = NONE)
@Import(OauthTestConfig::class)
class AuthServiceTest(
    private var authService: AuthService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("카카오 소셜 로그인을") {

        When("요청하면") {
            val authTokenInfo = authService.login(KAKAO, "accessCode")

            Then("멤버의 인증 토큰을 발급한다") {
                assertSoftly(authTokenInfo) {
                    authTokenProvider.isValidToken(accessToken) shouldBe true
                    authTokenProvider.isValidToken(refreshToken) shouldBe true
                }
            }

            Then("발급한 refreshToken을 저장한다") {
                refreshTokenRepository.existsByToken(authTokenInfo.refreshToken) shouldBe true
            }
        }
    }

    Given("로그인 연장을") {
        val member = memberRepository.save(member())
        val expiredAccessToken = authTokenProvider.createAuthToken(member, Date(0)).accessToken
        val refreshToken = authTokenProvider.createAuthToken(member, Date()).refreshToken
        println("exp : " + expiredAccessToken)
        println("ref : " + refreshToken)
        refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = refreshToken
            )
        )

        When("요청하면") {
            val authTokenInfo = authService.extendLogin(expiredAccessToken, refreshToken)

            Then("멤버의 인증 토큰을 발급한다") {
                authTokenProvider.isValidToken(authTokenInfo.accessToken) shouldBe true
                authTokenProvider.isValidToken(authTokenInfo.refreshToken) shouldBe true
            }

            Then("발급한 refreshToken을 저장한다") {
                refreshTokenRepository.existsByToken(authTokenInfo.refreshToken) shouldBe true
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
