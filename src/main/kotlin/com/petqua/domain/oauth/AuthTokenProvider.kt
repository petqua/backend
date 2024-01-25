package com.petqua.domain.oauth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

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
    private val properties: AuthTokenProperties
) {

    fun createAuthToken(memberId: Long): AuthToken {
        return AuthToken(
            accessToken = jwtProvider.createToken(memberId.toString(), properties.accessTokenLiveTime),
            refreshToken = jwtProvider.createToken(EMPTY_SUBJECT, properties.refreshTokenLiveTime)
        )
    }
}
