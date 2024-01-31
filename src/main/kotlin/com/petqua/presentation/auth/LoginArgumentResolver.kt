package com.petqua.presentation.auth

import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.exception.auth.AuthException
<<<<<<< HEAD
import com.petqua.common.exception.auth.AuthExceptionType.EMPTY_REFRESH_TOKEN_COOKIE
import com.petqua.common.exception.auth.AuthExceptionType.EXPIRED_TOKEN
import com.petqua.common.exception.auth.AuthExceptionType.INVALID_REFRESH_TOKEN
import com.petqua.common.exception.auth.AuthExceptionType.INVALID_REQUEST
=======
import com.petqua.common.exception.auth.AuthExceptionType
>>>>>>> 2617f49 (feature: 봉달목록에 상품 추가 api (#31))
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.token.AuthTokenProvider
import com.petqua.domain.auth.token.RefreshTokenRepository
import com.petqua.domain.member.MemberRepository
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
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
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
<<<<<<< HEAD
            ?: throw AuthException(INVALID_REQUEST)
        val refreshToken = request.cookies?.find {it.name == REFRESH_TOKEN_COOKIE}?.value
            ?: throw AuthException(EMPTY_REFRESH_TOKEN_COOKIE)
        val accessToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION) as String

        return try {
            val accessTokenClaims = authTokenProvider.getAccessTokenClaimsOrThrow(accessToken)
            LoginMember.from(accessTokenClaims)
        } catch (e: AuthException) {
            if (e.exceptionType() == EXPIRED_TOKEN) {
                loginByRefreshTokenWhenAccessTokenExpired(refreshToken)
            } else {
                throw e
            }
=======
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
>>>>>>> 2617f49 (feature: 봉달목록에 상품 추가 api (#31))
        }
    }

    private fun loginByRefreshTokenWhenAccessTokenExpired(refreshToken: String): LoginMember {
        val savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
            ?: throw AuthException(INVALID_REFRESH_TOKEN)

        if (savedRefreshToken.token != refreshToken) {
            throw AuthException(INVALID_REFRESH_TOKEN)
        }
        val member = memberRepository.findByIdOrThrow(savedRefreshToken.memberId)
        return LoginMember(member.id, member.authority)
    }
}
