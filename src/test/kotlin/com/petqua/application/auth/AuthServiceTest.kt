package com.petqua.application.auth

import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.test.config.OauthTestConfig
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Import

@SpringBootTest(webEnvironment = NONE)
@Import(OauthTestConfig::class)
class AuthServiceTest(
    private var authService: AuthService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProvider: JwtProvider,
) : BehaviorSpec({

    Given("소셜 로그인 테스트") {

        When("OauthServerType과 accessCode를 가지고 로그인을 하면") {
            val authTokenInfo = authService.login(KAKAO, "accessCode")

            Then("멤버의 인증 토큰을 발급한다") {
                assertSoftly(authTokenInfo) {
                    jwtProvider.isValidToken(accessToken) shouldBe true
                    jwtProvider.isValidToken(refreshToken) shouldBe true
                }
            }

            Then("발급한 refreshToken을 저장한다") {
                refreshTokenRepository.existsByToken(authTokenInfo.refreshToken) shouldBe true
            }
        }
    }
})
