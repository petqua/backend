package com.petqua.presentation.auth

import com.petqua.common.domain.existActiveByIdOrThrow
import com.petqua.common.util.getCookieValueOrThrow
import com.petqua.common.util.getHeaderOrThrow
import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.auth.token.AccessTokenClaims
import com.petqua.domain.auth.token.JwtProvider
import com.petqua.domain.member.MemberRepository
import com.petqua.exception.auth.AuthException
import com.petqua.exception.auth.AuthExceptionType.EXPIRED_ACCESS_TOKEN
import com.petqua.exception.auth.AuthExceptionType.INVALID_ACCESS_TOKEN
import com.petqua.exception.auth.AuthExceptionType.INVALID_AUTH_COOKIE
import com.petqua.exception.auth.AuthExceptionType.INVALID_AUTH_HEADER
import com.petqua.exception.member.MemberException
import com.petqua.exception.member.MemberExceptionType.NOT_FOUND_MEMBER
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component

private const val AUTHORIZATION_PREFIX = "Bearer "
private const val REFRESH_TOKEN = "refresh-token"

@Component
class AuthExtractor(
    private val jwtProvider: JwtProvider,
    private val memberRepository: MemberRepository,
) {

    fun hasAuthorizationHeader(request: HttpServletRequest): Boolean {
        return !request.getHeader(AUTHORIZATION).isNullOrBlank()
    }

    fun extractAccessToken(request: HttpServletRequest): String {
        val header = request.getHeaderOrThrow(AUTHORIZATION) { AuthException(INVALID_AUTH_HEADER) }
        return parse(header)
    }

    private fun parse(header: String): String {
        validateHeader(header)
        return header.removePrefix(AUTHORIZATION_PREFIX)
    }

    private fun validateHeader(header: String) {
        throwExceptionWhen(header.isBlank() || !header.startsWith(AUTHORIZATION_PREFIX))
        { AuthException(INVALID_AUTH_HEADER) }
    }

    fun extractRefreshToken(request: HttpServletRequest): String {
        return request.getCookieValueOrThrow(REFRESH_TOKEN) { AuthException(INVALID_AUTH_COOKIE) }
    }

    fun getAccessTokenClaimsOrThrow(token: String): AccessTokenClaims {
        try {
            val accessTokenClaims = AccessTokenClaims.from(jwtProvider.getPayload(token))
            memberRepository.existActiveByIdOrThrow(accessTokenClaims.memberId) {
                MemberException(NOT_FOUND_MEMBER)
            }
            return accessTokenClaims
        } catch (e: ExpiredJwtException) {
            throw AuthException(EXPIRED_ACCESS_TOKEN)
        } catch (e: JwtException) {
            throw AuthException(INVALID_ACCESS_TOKEN)
        } catch (e: NullPointerException) {
            throw AuthException(INVALID_ACCESS_TOKEN)
        }
    }
}
