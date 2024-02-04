package com.petqua.presentation.auth

import com.petqua.common.util.getHttpServletRequestOrThrow
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.token.AuthToken
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class TokenArgumentResolver(
    private val authExtractor: AuthExtractor,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
                && parameter.parameterType == AuthToken::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): AuthToken {
        val request = webRequest.getHttpServletRequestOrThrow()
        val accessToken = authExtractor.extractAccessToken(request)
        val refreshToken = authExtractor.extractRefreshToken(request)
        return AuthToken(accessToken, refreshToken)
    }
}
