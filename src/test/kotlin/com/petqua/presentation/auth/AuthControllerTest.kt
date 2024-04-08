package com.petqua.presentation.auth

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.auth.OauthService
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.auth.AuthCredentialsRepository
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.oauth.kakao.KakaoAccount
import com.petqua.domain.auth.oauth.kakao.KakaoOauthApiClient
import com.petqua.domain.auth.oauth.kakao.KakaoUserInfo
import com.petqua.domain.auth.oauth.kakao.Profile
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.member.nickname.Nickname
import com.petqua.exception.auth.AuthExceptionType.UNABLE_ACCESS_TOKEN
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.authCredentials
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.verify
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import java.util.Date

class AuthControllerTest(
    private val authCredentialsRepository: AuthCredentialsRepository,
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,

    @SpykBean private val kakaoOauthApiClient: KakaoOauthApiClient,
    @SpykBean private val oauthService: OauthService,
) : ApiTestConfig() {

    init {
        Given("소셜 로그인을 할 때") {

            When("회원이 카카오 로그인을 시도하면") {
                val oauthId = 1L
                val authCredentials = authCredentialsRepository.save(authCredentials(oauthId = oauthId))
                memberRepository.save(member(authCredentialsId = authCredentials.id))

                every {
                    kakaoOauthApiClient.fetchUserInfo(any(String::class))
                } returns KakaoUserInfo(
                    kakaoAccount = KakaoAccount(Profile(nickname = "nickname")),
                    oauthId = authCredentials.oauthId
                )

                val response = requestLogin(code = "accessCode")

                Then("200 OK로 응답한다") {
                    response.statusCode shouldBe OK.value()
                }

                Then("인증토큰이 반환된다.") {
                    val headers = response.headers()

                    headers.get(AUTHORIZATION) shouldNotBe null
                    headers.get(SET_COOKIE) shouldNotBe null
                }
            }

            When("비회원이 카카오 로그인을 시도하면") {
                val response = requestLogin(code = "accessCode")

                Then("201 CREATED로 응답한다") {
                    response.statusCode shouldBe CREATED.value()
                }

                Then("임시 토큰이 반환된다.") {
                    val signUpTokenResponse = response.`as`(SignUpTokenResponse::class.java)

                    signUpTokenResponse.signUpToken shouldNotBe null
                }
            }
        }

        Given("로그인 연장을") {
            val authCredentials = authCredentialsRepository.save(authCredentials())
            val member = memberRepository.save(
                member(
                    authCredentialsId = authCredentials.id
                )
            )
            val expiredAccessToken = authTokenProvider.createLoginAuthToken(
                member.id,
                member.authority,
                Date(0)
            ).getAccessToken()
            val refreshToken = authTokenProvider.createLoginAuthToken(
                member.id,
                member.authority,
                Date()
            ).getRefreshToken()
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = refreshToken
                )
            )

            When("요청하면") {
                val response = requestExtendLogin(
                    accessToken = expiredAccessToken,
                    refreshToken = refreshToken
                )

                Then("인증토큰이 반환된다.") {
                    val headers = response.headers()

                    response.statusCode shouldBe OK.value()
                    headers.get(AUTHORIZATION).shouldNotBeNull()
                    headers.get(SET_COOKIE).shouldNotBeNull()
                }
            }
        }

        Given("회원 탈퇴를 요청할 때") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            When("유효한 액세스 토큰으로 회원이 요청하면") {
                val response = requestDeleteMember(accessToken)

                Then("회원의 인증 정보가 삭제된다") {
                    val deletedMember = memberRepository.findByIdOrThrow(memberId)
                    val deletedAuthCredentials =
                        authCredentialsRepository.findByIdOrThrow(deletedMember.authCredentialsId)

                    assertSoftly(deletedAuthCredentials) {
                        response.statusCode shouldBe NO_CONTENT.value()

                        it.isDeleted shouldBe true
                        it.oauthId shouldBe -1L
                        it.oauthAccessToken shouldBe ""
                        it.oauthAccessTokenExpiresAt shouldBe null
                        it.oauthRefreshToken shouldBe ""
                    }
                }

                Then("회원의 개인 정보가 삭제된다") {
                    val deletedMember = memberRepository.findByIdOrThrow(memberId)

                    assertSoftly(deletedMember) {
                        it.nickname.value shouldBe Nickname.emptyNickname().value
                        it.profileImageUrl shouldBe null
                        it.isDeleted shouldBe true
                    }
                }

                Then("oauth 서버에 회원 정보 삭제를 요청한다") {
                    verify(exactly = 1) {
                        oauthService.disconnectBy(any(OauthServerType::class), any(String::class))
                    }
                }
            }
        }

        Given("로그아웃 요청을 할 때") {
            val authCredentials = authCredentialsRepository.save(authCredentials())
            val member = memberRepository.save(member(authCredentialsId = authCredentials.id))
            val createAuthToken = authTokenProvider.createLoginAuthToken(member.id, member.authority, Date())
            val accessToken = createAuthToken.getAccessToken()
            val refreshToken = createAuthToken.getRefreshToken()
            refreshTokenRepository.save(
                RefreshToken(
                    memberId = member.id,
                    token = refreshToken
                )
            )

            When("회원의 인증 정보를 입력 하면") {
                val response = requestSignOut(accessToken, refreshToken)
                val signOutAuthCredentials = authCredentialsRepository.findByIdOrThrow(member.id)

                Then("멤버의 토큰 정보와 RefreshToken이 초기화 된다") {
                    response.statusCode shouldBe NO_CONTENT.value()

                    assertSoftly(signOutAuthCredentials) {
                        refreshTokenRepository.findByToken(refreshToken) shouldBe null

                        it.oauthAccessToken shouldBe ""
                        it.oauthAccessTokenExpiresAt shouldBe null
                        it.oauthRefreshToken shouldBe ""
                    }
                }
            }
        }

        Given("로그아웃 된 인증정보로") {
            When("인증이 필요한 요청에 사용하는 경우") {
                val authCredentials = authCredentialsRepository.save(authCredentials())
                val member = memberRepository.save(member(authCredentialsId = authCredentials.id))
                val createAuthToken = authTokenProvider.createLoginAuthToken(member.id, member.authority, Date())
                val accessToken = createAuthToken.getAccessToken()
                val refreshToken = createAuthToken.getRefreshToken()
                refreshTokenRepository.save(
                    RefreshToken(
                        memberId = member.id,
                        token = refreshToken
                    )
                )
                requestSignOut(accessToken, refreshToken)

                val response = requestDeleteMember(accessToken)
                Then("사용 할 수 없다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe UNAUTHORIZED.value()
                        errorResponse.message shouldBe UNABLE_ACCESS_TOKEN.errorMessage()
                    }.statusCode shouldBe 401
                }
            }

            When("로그인 연장 요청시에 사용하는 경우") {
                val authCredentials = authCredentialsRepository.save(authCredentials())
                val member = memberRepository.save(member(authCredentialsId = authCredentials.id))
                val createAuthToken = authTokenProvider.createLoginAuthToken(member.id, member.authority, Date())
                val accessToken = createAuthToken.getAccessToken()
                val refreshToken = createAuthToken.getRefreshToken()
                refreshTokenRepository.save(
                    RefreshToken(
                        memberId = member.id,
                        token = refreshToken
                    )
                )
                requestSignOut(accessToken, refreshToken)

                val response = requestExtendLogin(accessToken, refreshToken)
                Then("사용 할 수 없다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)
                    assertSoftly(response) {
                        statusCode shouldBe UNAUTHORIZED.value()
                        errorResponse.message shouldBe UNABLE_ACCESS_TOKEN.errorMessage()
                    }.statusCode shouldBe 401
                }
            }
        }
    }
}
