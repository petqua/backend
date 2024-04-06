package com.petqua.presentation.auth

import com.petqua.common.util.getHttpServletRequestOrThrow
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.SignUpGuest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class SignUpArgumentResolver(
    private val authExtractor: AuthExtractor,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
                && parameter.parameterType == SignUpGuest::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): SignUpGuest {
        val request = webRequest.getHttpServletRequestOrThrow()
        val token = authExtractor.extractSignUpToken(request)
        val signUpTokenClaims = authExtractor.getSignUpTokenClaimsOrThrow(token)
        return SignUpGuest(signUpTokenClaims.authCredentialsId)
    }
}
