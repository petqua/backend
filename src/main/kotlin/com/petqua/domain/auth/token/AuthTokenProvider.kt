package com.petqua.domain.auth.token

import com.petqua.common.exception.auth.AuthException
import com.petqua.common.exception.auth.AuthExceptionType.EXPIRED_TOKEN
import com.petqua.common.exception.auth.AuthExceptionType.INVALID_ACCESS_TOKEN
import com.petqua.common.exception.auth.AuthExceptionType.INVALID_TOKEN
import com.petqua.domain.member.Member
import io.jsonwebtoken.ExpiredJwtException
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

    fun isValidToken(token: String): Boolean {
        return jwtProvider.isValidToken(token)
    }

    fun getAccessTokenClaims(token: String): AccessTokenClaims {
        try {
            return AccessTokenClaims.from(jwtProvider.getPayload(token))
        } catch (e: ExpiredJwtException) {
            throw AuthException(EXPIRED_TOKEN)
        } catch (e: JwtException) {
            throw AuthException(INVALID_TOKEN)
        } catch (e: NullPointerException) {
            throw AuthException(INVALID_ACCESS_TOKEN)
        }
    }
}
