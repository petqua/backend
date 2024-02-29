package com.petqua.domain.auth.token

import com.petqua.domain.member.Member
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.EXPIRED_REFRESH_TOKEN
import com.petqua.exception.auth.AuthExceptionType.INVALID_ACCESS_TOKEN
import com.petqua.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import com.petqua.exception.auth.AuthExceptionType.NOT_RENEWABLE_ACCESS_TOKEN
import io.jsonwebtoken.JwtException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.Date

private const val EMPTY_SUBJECT = ""

@ConfigurationProperties("token")
data class AuthTokenProperties(
    val accessTokenLiveTime: Long,
    val refreshTokenLiveTime: Long,
)

@EnableConfigurationProperties(AuthTokenProperties::class)
@Component
class AuthTokenProvider(
    private val jwtProvider: JwtProvider,
    private val properties: AuthTokenProperties,
) {

    fun createAuthToken(member: Member, issuedDate: Date): AuthToken {
        val accessToken = AccessTokenClaims(member.id, member.authority)
        return AuthToken(
            accessToken = jwtProvider.createToken(accessToken.getClaims(), properties.accessTokenLiveTime, issuedDate),
            refreshToken = jwtProvider.createToken(EMPTY_SUBJECT, properties.refreshTokenLiveTime, issuedDate)
        )
    }

    fun validateTokenExpiredStatusForExtendLogin(accessToken: String, refreshToken: String) {
        if (!isExpiredAccessToken(accessToken)) {
            throw AuthException(NOT_RENEWABLE_ACCESS_TOKEN)
        }
        if (isExpiredRefreshToken(refreshToken)) {
            throw AuthException(EXPIRED_REFRESH_TOKEN)
        }
    }

    private fun isExpiredAccessToken(token: String): Boolean {
        try {
            return jwtProvider.isExpiredToken(token)
        } catch (e: JwtException) {
            throw AuthException(INVALID_ACCESS_TOKEN)
        }
    }

    private fun isExpiredRefreshToken(token: String): Boolean {
        try {
            return jwtProvider.isExpiredToken(token)
        } catch (e: JwtException) {
            throw AuthException(INVALID_REFRESH_TOKEN)
        }
    }
}
