package com.petqua.application.auth

import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType
import com.petqua.exception.member.MemberException
import com.petqua.test.DataCleaner
import com.petqua.test.config.OauthTestConfig
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Import
import java.lang.System.currentTimeMillis
import java.util.*

@SpringBootTest(webEnvironment = NONE)
@Import(OauthTestConfig::class)
class AuthFacadeServiceTest(
    private val authFacadeService: AuthFacadeService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val jwtProvider: JwtProvider,
    private val memberRepository: MemberRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("카카오 소셜 로그인을") {

        When("요청하면") {
            val authTokenInfo = authFacadeService.login(KAKAO, "accessCode")

            Then("멤버의 인증 토큰을 발급한다") {
                assertSoftly(authTokenInfo) {
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(accessToken)
                    }
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(refreshToken)
                    }
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
        refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = refreshToken
            )
        )

        When("요청하면") {
            val authTokenInfo = authFacadeService.extendLogin(expiredAccessToken, refreshToken)

            Then("멤버의 인증 토큰을 발급한다") {
                assertSoftly(authTokenInfo) {
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(accessToken)
                    }
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(refreshToken)
                    }
                }
            }

            Then("발급한 refreshToken을 저장한다") {
                refreshTokenRepository.existsByToken(authTokenInfo.refreshToken) shouldBe true
            }
        }
    }

    Given("로그인 연장 요청시") {
        val member = memberRepository.save(member())

        When("AccessToken이 만료되지 않은 경우") {
            val authToken = authTokenProvider.createAuthToken(member, Date())
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = authToken.refreshToken
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(authToken.accessToken, authToken.refreshToken)
                }.exceptionType() shouldBe AuthExceptionType.NOT_RENEWABLE_ACCESS_TOKEN
            }
        }

        When("RefreshToken이 만료된 경우") {
            val expiredAuthToken = authTokenProvider.createAuthToken(member, Date(0))
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = expiredAuthToken.refreshToken
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(expiredAuthToken.accessToken, expiredAuthToken.refreshToken)
                }.exceptionType() shouldBe AuthExceptionType.EXPIRED_REFRESH_TOKEN
            }
        }

        When("RefreshToken이 저장되어있지 않은 경우") {
            val expiredAccessToken = authTokenProvider.createAuthToken(member, Date(0)).accessToken
            val unsavedRefreshToken = authTokenProvider.createAuthToken(member, Date()).refreshToken

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(expiredAccessToken, unsavedRefreshToken)
                }.exceptionType() shouldBe AuthExceptionType.INVALID_REFRESH_TOKEN
            }
        }

        When("RefreshToken이 저장된 토큰값과 다른 경우") {
            val expiredAccessToken = authTokenProvider.createAuthToken(member, Date(0)).accessToken
            val oneMinuteAgoMillSec = currentTimeMillis() - 60 * 1000
            val unsavedRefreshToken = authTokenProvider.createAuthToken(member, Date(oneMinuteAgoMillSec)).refreshToken
            val refreshToken = authTokenProvider.createAuthToken(member, Date()).refreshToken
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = refreshToken
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(expiredAccessToken, unsavedRefreshToken)
                }.exceptionType() shouldBe AuthExceptionType.INVALID_REFRESH_TOKEN
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
