package com.petqua.presentation.auth

import com.petqua.common.util.getHttpServletRequestOrThrow
import com.petqua.domain.auth.Auth
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
                && parameter.parameterType == LoginTokenRequest::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): LoginTokenRequest {
        val request = webRequest.getHttpServletRequestOrThrow()
        val accessToken = authExtractor.extractAccessToken(request)
        authExtractor.validateBlacklistTokenRegardlessExpiration(accessToken)
        val refreshToken = authExtractor.extractRefreshToken(request)
        return LoginTokenRequest(accessToken, refreshToken)
    }
}
