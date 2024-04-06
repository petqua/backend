package com.petqua.application.auth

import com.ninjasquad.springmockk.SpykBean
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.AuthMemberRepository
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
import com.petqua.test.fixture.authMember
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import java.lang.System.currentTimeMillis
import java.time.LocalDateTime
import java.util.Date
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE)
class AuthFacadeServiceTest(
    private val authFacadeService: AuthFacadeService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,
    private val jwtProvider: JwtProvider,
    private val authMemberRepository: AuthMemberRepository,
    private val memberRepository: MemberRepository,
    private val blackListTokenCacheStorage: BlackListTokenCacheStorage,
    private val dataCleaner: DataCleaner,

    @SpykBean private val kakaoOauthApiClient: KakaoOauthApiClient,
    @SpykBean private val oauthService: OauthService,
) : BehaviorSpec({

    Given("카카오 소셜 로그인을") {

        When("회원이 요청하면") {
            val authMember = authMemberRepository.save(authMember(oauthId = 1L))
            memberRepository.save(member(authMemberId = authMember.id))

            every {
                kakaoOauthApiClient.fetchUserInfo(any(String::class))
            } returns KakaoUserInfo(
                kakaoAccount = KakaoAccount(Profile(nickname = "nickname")),
                oauthId = authMember.oauthId
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
                        jwtProvider.parseToken(accessToken)
                    }
                    refreshToken.isEmpty() shouldBe true
                }
            }
        }
    }

    Given("로그인 연장을") {
        val authMember = authMemberRepository.save(authMember())
        val member = memberRepository.save(member(authMemberId = authMember.id))
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
        val authMember = authMemberRepository.save(authMember())
        val member = memberRepository.save(member(authMemberId = authMember.id))

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

        When("kakao oauthAccessToken 이 만료된 경우") {
            val expiredAuthMember = authMemberRepository.save(
                authMember(
                    oauthId = 1L,
                    oauthServerNumber = KAKAO.number,
                    oauthAccessToken = "expiredOauthAccessToken",
                    oauthAccessTokenExpiresAt = LocalDateTime.of(2024, 1, 1, 0, 0),
                    oauthRefreshToken = "oauthRefreshToken",
                )
            )
            val expiredMember = memberRepository.save(
                member(
                    authMemberId = expiredAuthMember.id
                )
            )
            val expiredAccessToken = authTokenProvider.createAuthToken(expiredMember, Date(0)).accessToken
            val refreshToken = authTokenProvider.createAuthToken(expiredMember, Date()).refreshToken
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = expiredAuthMember.id,
                    token = refreshToken
                )
            )

            authFacadeService.extendLogin(expiredAccessToken, refreshToken)

            Then("토큰 정보를 갱신한다") {
                val updatedMember = authMemberRepository.findByIdOrThrow(expiredAuthMember.id)

                assertSoftly(updatedMember) {
                    updatedMember.oauthAccessToken shouldBe "oauthAccessToken"
                    updatedMember.hasExpiredOauthToken() shouldBe false
                }
                verify(exactly = 1) {
                    oauthService.updateOauthToken(KAKAO, "oauthRefreshToken")
                }
            }
        }
    }

    Given("회원을 서비스에서 탈퇴시킬 때") {
        val authMember = authMemberRepository.save(
            authMember(
                oauthId = 1L,
                oauthServerNumber = KAKAO.number,
                oauthAccessToken = "oauthAccessToken",
                oauthAccessTokenExpiresAt = LocalDateTime.now().plusSeconds(21599),
                oauthRefreshToken = "oauthAccessToken",
            )
        )
        val member = memberRepository.save(
            member(
                authMemberId = authMember.id
            )
        )

        When("회원의 id를 입력하면") {
            authFacadeService.deleteBy(member.id)

            Then("입력한 회원의 인증 정보를 삭제한다") {
                val deletedAuthMember = authMemberRepository.findByIdOrThrow(authMember.id)


                assertSoftly(deletedAuthMember) {
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
                    it.nickname shouldBe Nickname.emptyNickname()
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
            val expiredAuthMember = authMemberRepository.save(
                authMember(
                    oauthId = 1L,
                    oauthServerNumber = KAKAO.number,
                    oauthAccessToken = "expiredOauthAccessToken",
                    oauthAccessTokenExpiresAt = LocalDateTime.of(2024, 1, 1, 0, 0),
                    oauthRefreshToken = "oauthRefreshToken",
                )
            )
            val expiredMember = memberRepository.save(
                member(
                    authMemberId = expiredAuthMember.id
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
        val member = memberRepository.save(member())
        val accessToken = authTokenProvider.createAuthToken(member, Date()).accessToken
        val refreshToken = authTokenProvider.createAuthToken(member, Date()).refreshToken
        refreshTokenRepository.save(
            RefreshToken(
                memberId = member.id,
                token = refreshToken
            )
        )

        When("회원의 인증 정보를 입력 하면") {
            authFacadeService.logOut(accessToken, refreshToken)

            Then("멤버의 토큰 정보와 RefreshToken이 초기화 된다") {
                val signedOutMember = memberRepository.findByIdOrThrow(member.id)

                assertSoftly(signedOutMember) {
                    refreshTokenRepository.existsByToken(refreshToken) shouldBe false
                    it.oauthAccessToken shouldBe ""
                    it.oauthAccessTokenExpiresAt shouldBe null
                    it.oauthRefreshToken shouldBe ""
                }
            }

            Then("로그아웃한 회원의 토큰 정보를 블랙리스트에 추가한다") {
                blackListTokenCacheStorage.isBlackListed(member.id, accessToken) shouldBe true
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
