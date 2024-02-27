package com.petqua.application.auth

import com.ninjasquad.springmockk.SpykBean
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.test.DataCleaner
import com.petqua.test.config.OauthTestConfig
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.context.annotation.Import
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
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

    @SpykBean private val oauthService: OauthService,
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

    Given("회원을 서비스에서 탈퇴시킬 때") {
        val member = memberRepository.save(
            member(
                oauthId = 1L,
                oauthServerNumber = KAKAO.number,
                oauthAccessToken = "oauthAccessToken",
                expireAt = LocalDateTime.now().plusSeconds(21599),
                oauthRefreshToken = "oauthAccessToken",
            )
        )

        When("회원의 id를 입력하면") {
            authFacadeService.deleteBy(member.id)

            Then("입력한 회원의 정보를 삭제한다") {
                val deletedMember = memberRepository.findByIdOrThrow(member.id)

                assertSoftly(deletedMember) {
                    it.isDeleted shouldBe true

                    it.oauthId shouldBe -1L
                    it.nickname shouldBe ""
                    it.profileImageUrl shouldBe null
                    it.oauthAccessToken shouldBe ""
                    it.expireAt shouldBe null
                    it.oauthRefreshToken shouldBe ""
                }
            }
        }

        When("존재하지 않는 회원의 id를 입력하면") {
            val memberId = Long.MIN_VALUE

            Then("입력한 회원의 정보를 삭제한다") {
                shouldThrow<MemberException> {
                    authFacadeService.deleteBy(memberId)
                }.exceptionType() shouldBe NOT_FOUND_MEMBER
            }
        }

        When("회원의 oauth 토큰이 만료되었으면") {
            val expiredMember = memberRepository.save(
                member(
                    oauthId = 1L,
                    oauthServerNumber = KAKAO.number,
                    oauthAccessToken = "expiredOauthAccessToken",
                    expireAt = LocalDateTime.of(2024, 1, 1, 0, 0),
                    oauthRefreshToken = "oauthRefreshToken",
                )
            )

            authFacadeService.deleteBy(expiredMember.id)

            Then("토큰 정보를 갱신한다") {
                verify(exactly = 1) {
                    oauthService.updateOauthToken(KAKAO, "oauthRefreshToken")
                }
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
