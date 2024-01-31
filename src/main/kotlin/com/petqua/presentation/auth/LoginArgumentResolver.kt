package com.petqua.presentation.auth

import com.petqua.common.exception.auth.AuthException
import com.petqua.common.exception.auth.AuthExceptionType
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshTokenRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

private const val REFRESH_TOKEN_COOKIE = "refresh-token"

@Component
class LoginArgumentResolver(
    private val authTokenProvider: AuthTokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
                && parameter.getParameterType() == LoginMember::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): LoginMember {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw AuthException(AuthExceptionType.INVALID_REQUEST)
        val refreshToken = request.cookies?.find { it.name == REFRESH_TOKEN_COOKIE }?.value
        val accessToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION) as String
        val accessTokenClaims = authTokenProvider.getAccessTokenClaims(accessToken)
        if (refreshToken == null) {
            return LoginMember.from(accessTokenClaims)
        }

        val savedRefreshToken = refreshTokenRepository.findByMemberId(accessTokenClaims.memberId)
            ?: throw AuthException(AuthExceptionType.INVALID_REFRESH_TOKEN)
        if (savedRefreshToken.token == refreshToken) {
            return LoginMember.from(accessTokenClaims)
        }
        throw AuthException(AuthExceptionType.INVALID_REFRESH_TOKEN)
    }
}
