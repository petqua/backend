package com.petqua.presentation.auth

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.auth.OauthService
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshToken
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.member
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import java.util.Date

class AuthControllerTest(
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenProvider: AuthTokenProvider,
    @SpykBean private val oauthService: OauthService,
) : ApiTestConfig() {

    init {
        Given("소셜 로그인을 할 때") {

            When("카카오 로그인을 시도하면") {
                val response = requestLogin(code = "accessCode")

                Then("인증토큰이 반환된다.") {
                    val headers = response.headers()

                    response.statusCode shouldBe OK.value()
                    headers.get(AUTHORIZATION).shouldNotBeNull()
                    headers.get(SET_COOKIE).shouldNotBeNull()
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

                Then("회원이 삭제된다") {
                    val deletedMember = memberRepository.findByIdOrThrow(memberId)

                    assertSoftly(deletedMember) {
                        response.statusCode shouldBe NO_CONTENT.value()

                        it.isDeleted shouldBe true
                        it.oauthId shouldBe -1L
                        it.nickname shouldBe ""
                        it.profileImageUrl shouldBe null
                        it.oauthAccessToken shouldBe ""
                        it.oauthAccessTokenExpiresAt shouldBe null
                        it.oauthRefreshToken shouldBe ""
                    }
                }

                Then("oauth 서버에 회원 정보 삭제를 요청한다") {
                    verify(exactly = 1) {
                        oauthService.disconnectBy(any(OauthServerType::class), any(String::class))
                    }
                }
            }
        }
    }
}
