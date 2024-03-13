package com.petqua.domain.auth

import com.petqua.domain.auth.token.AccessTokenClaims
import com.petqua.domain.auth.token.AuthTokenProperties
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.exception.member.MemberException
import com.petqua.test.fixture.member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@SpringBootTest
class AuthTokenProviderTest(
    authTokenProvider: AuthTokenProvider,
    properties: AuthTokenProperties,
    jwtProvider: JwtProvider,
) : BehaviorSpec({

    Given("인증 토큰 발급 테스트") {
        val issuedLocalDate = LocalDate.of(3000, 1, 1)
        val issuedDate = Date.from(issuedLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val member = member(id = 1L)

        When("인증 토큰을 발급하면") {
            val authToken = authTokenProvider.createAuthToken(member, issuedDate)

            Then("JWT타입인 accessToken과 refreshToken이 발급된다") {
                val accessTokenExpirationTime = parseExpirationTime(jwtProvider.parseToken(authToken.accessToken))
                val refreshTokenExpirationTime = parseExpirationTime(jwtProvider.parseToken(authToken.refreshToken))
                val accessTokenClaims = AccessTokenClaims.from(jwtProvider.getPayload(authToken.accessToken))

                assertSoftly {
                    accessTokenClaims.memberId shouldBe member.id
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(authToken.accessToken)
                    }
                    shouldNotThrow<MemberException> {
                        jwtProvider.parseToken(authToken.refreshToken)
                    }
                    accessTokenExpirationTime shouldBe calculateExpirationTime(
                        issuedDate,
                        properties.accessTokenLiveTime
                    )
                    refreshTokenExpirationTime shouldBe calculateExpirationTime(
                        issuedDate,
                        properties.refreshTokenLiveTime
                    )
                }
            }
        }
    }
}
)

fun parseExpirationTime(claims: Jws<Claims>): LocalDateTime? {
    val expirationTimeMillis = claims.body["exp"] as? Long ?: return null
    val expirationInstant = Instant.ofEpochSecond(expirationTimeMillis)
    return LocalDateTime.ofInstant(expirationInstant, ZoneOffset.UTC)
}

fun calculateExpirationTime(date: Date, milliseconds: Long): LocalDateTime {
    val instant = Instant.ofEpochMilli(date.time + milliseconds)
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
}
