package com.petqua.presentation.auth

import com.petqua.common.exception.auth.AuthException
import com.petqua.common.exception.auth.AuthExceptionType
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.token.AuthTokenProvider
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoginArgumentResolver(
    private val authTokenProvider: AuthTokenProvider,
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
        val accessToken = webRequest.getHeader(AUTHORIZATION) as String
        val accessTokenClaims = authTokenProvider.getAccessTokenClaimsOrThrow(accessToken)
        return LoginMember.from(accessTokenClaims)
    }
}
