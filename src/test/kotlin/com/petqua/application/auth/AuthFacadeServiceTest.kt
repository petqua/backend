package com.petqua.application.auth

import com.ninjasquad.springmockk.SpykBean
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.AuthCredentialsRepository
import com.petqua.domain.auth.oauth.OauthServerType.KAKAO
import com.petqua.domain.auth.oauth.kakao.KakaoAccount
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoUserInfo
import com.petqua.domain.auth.oauth.kakao.Profile
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.BlackListTokenCacheStorage
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.nickname.Nickname
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.authCredentials
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
import java.util.Date

@SpringBootTest(webEnvironment = NONE)
class AuthFacadeServiceTest(
    private val authFacadeService: AuthFacadeService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val jwtProvider: JwtProvider,
    private val authCredentialsRepository: AuthCredentialsRepository,
    private val memberRepository: MemberRepository,
    private val blackListTokenCacheStorage: BlackListTokenCacheStorage,
    private val dataCleaner: DataCleaner,

    @SpykBean private val kakaoOauthApiClient: KakaoOauthApiClient,
    @SpykBean private val oauthService: OauthService,
) : BehaviorSpec({

    Given("카카오 소셜 로그인을") {

        When("회원이 요청하면") {
            val authCredentials = authCredentialsRepository.save(authCredentials(oauthId = 1L))
            memberRepository.save(member(authCredentialsId = authCredentials.id))

            every {
                kakaoOauthApiClient.fetchUserInfo(any(String::class))
            } returns KakaoUserInfo(
                kakaoAccount = KakaoAccount(Profile(nickname = "nickname")),
                oauthId = authCredentials.oauthId
            )

            val authTokenInfo = authFacadeService.login(KAKAO, "accessCode")

            Then("회원의 인증 토큰을 발급한다") {
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

        When("비회원이 요청하면") {
            val authTokenInfo = authFacadeService.login(KAKAO, "accessCode")

            Then("임시 토큰을 발급한다") {
                assertSoftly(authTokenInfo) {
                    isSignUpNeeded() shouldBe true
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(signUpToken)
                    }
                }
            }
        }
    }

    Given("로그인 연장을") {
        val authCredentials = authCredentialsRepository.save(authCredentials())
        val member = memberRepository.save(member(authCredentialsId = authCredentials.id))
        val expiredAccessToken = authTokenProvider.createLoginAuthToken(
            memberId = member.id,
            authority = member.authority,
            issuedDate = Date(0)
        ).getAccessToken()
        val refreshToken = authTokenProvider.createLoginAuthToken(
            memberId = member.id,
            authority = member.authority,
            issuedDate = Date()
        ).getRefreshToken()
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
        val authCredentials = authCredentialsRepository.save(authCredentials())
        val member = memberRepository.save(member(authCredentialsId = authCredentials.id))

        When("AccessToken이 만료되지 않은 경우") {
            val authToken = authTokenProvider.createLoginAuthToken(
                memberId = member.id,
                authority = member.authority,
                issuedDate = Date()
            )
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = authToken.getRefreshToken()
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(authToken.getAccessToken(), authToken.getRefreshToken())
                }.exceptionType() shouldBe AuthExceptionType.NOT_RENEWABLE_ACCESS_TOKEN
            }
        }

        When("RefreshToken이 만료된 경우") {
            val expiredAuthToken = authTokenProvider.createLoginAuthToken(
                memberId = member.id,
                authority = member.authority,
                issuedDate = Date(0)
            )
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = expiredAuthToken.getRefreshToken()
                )
            )

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(expiredAuthToken.getAccessToken(), expiredAuthToken.getRefreshToken())
                }.exceptionType() shouldBe AuthExceptionType.EXPIRED_REFRESH_TOKEN
            }
        }

        When("RefreshToken이 저장되어있지 않은 경우") {
            val expiredAccessToken = authTokenProvider.createLoginAuthToken(
                memberId = member.id,
                authority = member.authority,
                issuedDate = Date(0)
            ).getAccessToken()
            val unsavedRefreshToken =
                authTokenProvider.createLoginAuthToken(
                    memberId = member.id,
                    authority = member.authority,
                    issuedDate = Date()
                ).getRefreshToken()

            Then("예외가 발생한다") {
                shouldThrow<AuthException> {
                    authFacadeService.extendLogin(expiredAccessToken, unsavedRefreshToken)
                }.exceptionType() shouldBe AuthExceptionType.INVALID_REFRESH_TOKEN
            }
        }

        When("RefreshToken이 저장된 토큰값과 다른 경우") {
            val expiredAccessToken = authTokenProvider.createLoginAuthToken(
                memberId = member.id,
                authority = member.authority,
                issuedDate = Date(0)
            ).getAccessToken()
            val oneMinuteAgoMillSec = currentTimeMillis() - 60 * 1000
            val unsavedRefreshToken =
                authTokenProvider.createLoginAuthToken(
                    memberId = member.id,
                    authority = member.authority,
                    issuedDate = Date(oneMinuteAgoMillSec)
                ).getRefreshToken()
            val refreshToken = authTokenProvider.createLoginAuthToken(
                memberId = member.id,
                authority = member.authority,
                issuedDate = Date()
            ).getRefreshToken()
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

        When("kakao oauthAccessToken 이 만료된 경우") {
            val expiredAuthCredentials = authCredentialsRepository.save(
                authCredentials(
                    oauthId = 1L,
                    oauthServerNumber = KAKAO.number,
                    oauthAccessToken = "expiredOauthAccessToken",
                    oauthAccessTokenExpiresAt = LocalDateTime.of(2024, 1, 1, 0, 0),
                    oauthRefreshToken = "oauthRefreshToken",
                )
            )
            val expiredMember = memberRepository.save(
                member(
                    authCredentialsId = expiredAuthCredentials.id
                )
            )
            val expiredAccessToken = authTokenProvider.createLoginAuthToken(
                memberId = expiredMember.id,
                authority = expiredMember.authority,
                issuedDate = Date(0)
            ).getAccessToken()
            val refreshToken = authTokenProvider.createLoginAuthToken(
                memberId = expiredMember.id,
                authority = expiredMember.authority,
                issuedDate = Date()
            ).getRefreshToken()
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = expiredMember.id,
                    token = refreshToken
                )
            )

            authFacadeService.extendLogin(expiredAccessToken, refreshToken)

            Then("토큰 정보를 갱신한다") {
                val updatedAuthCredentials = authCredentialsRepository.findByIdOrThrow(expiredAuthCredentials.id)

                assertSoftly(updatedAuthCredentials) {
                    updatedAuthCredentials.oauthAccessToken shouldBe "oauthAccessToken"
                    updatedAuthCredentials.hasExpiredOauthToken() shouldBe false
                }
                verify(exactly = 1) {
                    oauthService.updateOauthToken(KAKAO, "oauthRefreshToken")
                }
            }
        }
    }

    Given("회원을 서비스에서 탈퇴시킬 때") {
        val authCredentials = authCredentialsRepository.save(
            authCredentials(
                oauthId = 1L,
                oauthServerNumber = KAKAO.number,
                oauthAccessToken = "oauthAccessToken",
                oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(21599),
                oauthRefreshToken = "oauthAccessToken",
            )
        )
        val member = memberRepository.save(
            member(
                authCredentialsId = authCredentials.id
            )
        )

        When("회원의 id를 입력하면") {
            authFacadeService.deleteBy(member.id)

            Then("입력한 회원의 인증 정보를 삭제한다") {
                val deletedAuthCredentials = authCredentialsRepository.findByIdOrThrow(authCredentials.id)

                assertSoftly(deletedAuthCredentials) {
                    it.isDeleted shouldBe true
                    it.oauthId shouldBe -1L
                    it.oauthAccessToken shouldBe ""
                    it.oauthAccessTokenExpiresAt shouldBe null
                    it.oauthRefreshToken shouldBe ""
                }
            }

            Then("입력한 회원의 개인 정보를 삭제한다") {
                val deletedMember = memberRepository.findByIdOrThrow(member.id)

                assertSoftly(deletedMember) {
                    it.nickname.value shouldBe Nickname.emptyNickname().value
                    it.profileImageUrl shouldBe null
                    it.isDeleted shouldBe true
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
            val expiredAuthCredentials = authCredentialsRepository.save(
                authCredentials(
                    oauthId = 1L,
                    oauthServerNumber = KAKAO.number,
                    oauthAccessToken = "expiredOauthAccessToken",
                    oauthAccessTokenExpiresAt = LocalDateTime.of(2024, 1, 1, 0, 0),
                    oauthRefreshToken = "oauthRefreshToken",
                )
            )
            val expiredMember = memberRepository.save(
                member(
                    authCredentialsId = expiredAuthCredentials.id
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

    Given("로그아웃을 요청 시") {
        val authCredentials = authCredentialsRepository.save(authCredentials())
        val member = memberRepository.save(member(authCredentialsId = authCredentials.id))
        val accessToken = authTokenProvider.createLoginAuthToken(member.id, member.authority, Date()).getAccessToken()
        val refreshToken = authTokenProvider.createLoginAuthToken(member.id, member.authority, Date()).getRefreshToken()
        refreshTokenRepository.save(
            RefreshToken(
                memberId = authCredentials.id,
                token = refreshToken
            )
        )

        When("회원의 인증 정보를 입력 하면") {
            authFacadeService.logOut(accessToken, refreshToken)

            Then("멤버의 토큰 정보와 RefreshToken이 초기화 된다") {
                val signedOutAuthCredentials = authCredentialsRepository.findByIdOrThrow(authCredentials.id)

                assertSoftly(signedOutAuthCredentials) {
                    refreshTokenRepository.existsByToken(refreshToken) shouldBe false
                    it.oauthAccessToken shouldBe ""
                    it.oauthAccessTokenExpiresAt shouldBe null
                    it.oauthRefreshToken shouldBe ""
                }
            }

            Then("로그아웃한 회원의 토큰 정보를 블랙리스트에 추가한다") {
                blackListTokenCacheStorage.isBlackListed(authCredentials.id, accessToken) shouldBe true
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
