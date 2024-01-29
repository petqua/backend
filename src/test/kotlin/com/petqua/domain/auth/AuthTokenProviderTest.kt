package com.petqua.domain.auth

import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.token.AuthTokenProperties
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.domain.member.Member
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
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
        val member = Member(1L, "oauthId", OauthServerType.KAKAO.number, Authority.MEMBER)

        When("인증 토큰을 발급하면") {
            val authToken = authTokenProvider.createAuthToken(member, issuedDate)
            val accessTokenExpirationTime = parseExpirationTime(jwtProvider.parseToken(authToken.accessToken))
            val refreshTokenExpirationTime = parseExpirationTime(jwtProvider.parseToken(authToken.refreshToken))
            val accessTokenClaims = authTokenProvider.getAccessTokenClaims(authToken.accessToken)

            Then("JWT타입인 accessToken과 refreshToken이 발급된다") {
                accessTokenClaims.memberId shouldBe member.id
                accessTokenClaims.authority shouldBe member.authority
                authTokenProvider.isValidToken(authToken.accessToken) shouldBe true
                authTokenProvider.isValidToken(authToken.refreshToken) shouldBe true
                accessTokenExpirationTime shouldBe calculateExpirationTime(issuedDate, properties.accessTokenLiveTime)
                refreshTokenExpirationTime shouldBe calculateExpirationTime(issuedDate, properties.refreshTokenLiveTime)
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