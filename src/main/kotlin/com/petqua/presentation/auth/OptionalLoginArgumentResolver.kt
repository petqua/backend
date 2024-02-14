package com.petqua.presentation.auth

import com.petqua.common.util.getHttpServletRequestOrThrow
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMemberOrGuest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class OptionalLoginArgumentResolver(
    private val authExtractor: AuthExtractor,
) : HandlerMethodArgumentResolver {
    
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
                && parameter.parameterType == LoginMemberOrGuest::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): LoginMemberOrGuest {
        val request = webRequest.getHttpServletRequestOrThrow()

        if (authExtractor.hasAuthorizationHeader(request)) {
            val token = authExtractor.extractAccessToken(request)
            val accessTokenClaims = authExtractor.getAccessTokenClaimsOrThrow(token)
            return LoginMemberOrGuest.getMemberFrom(accessTokenClaims)
        }

        return LoginMemberOrGuest.getGuest()
    }
}
