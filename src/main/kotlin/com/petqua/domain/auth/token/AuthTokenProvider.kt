package com.petqua.domain.auth.token

import com.petqua.common.exception.auth.AuthTokenException
import com.petqua.common.exception.auth.AuthTokenExceptionType.EXPIRED_TOKEN
import com.petqua.common.exception.auth.AuthTokenExceptionType.INVALID_ACCESS_TOKEN
import com.petqua.common.exception.auth.AuthTokenExceptionType.INVALID_TOKEN
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*

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

    fun createAuthToken(memberId: Long, issuedDate: Date): AuthToken {
        return AuthToken(
            accessToken = jwtProvider.createToken(memberId.toString(), properties.accessTokenLiveTime, issuedDate),
            refreshToken = jwtProvider.createToken(EMPTY_SUBJECT, properties.refreshTokenLiveTime, issuedDate)
        )
    }

    fun isValidToken(token: String): Boolean {
        return jwtProvider.isValidToken(token)
    }

    fun getMemberIdFromAccessToken(token: String): Long {
        try {
            return jwtProvider.getSubject(token).toLong()
        } catch (e: ExpiredJwtException) {
            throw AuthTokenException(EXPIRED_TOKEN)
        } catch (e: JwtException) {
            throw AuthTokenException(INVALID_TOKEN)
        } catch (e: NullPointerException) {
            throw AuthTokenException(INVALID_ACCESS_TOKEN)
        }
    }
}
