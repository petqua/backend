package com.petqua.presentation.auth

import com.petqua.application.auth.AuthService
import com.petqua.application.auth.AuthTokenInfo
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.token.AuthToken
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.FOUND
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 관련 API 명세(Swagger에서 테스트할 수 없어 숨김 처리 되었습니다.)")
@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @Hidden
    @GetMapping("/{oauthServerType}")
    fun redirectToAuthCodeRequestUrl(
        @PathVariable oauthServerType: OauthServerType,
    ): ResponseEntity<Unit> {
        val redirectUri = authService.getAuthCodeRequestUrl(oauthServerType)
        return ResponseEntity.status(FOUND)
            .location(redirectUri)
            .build()
    }

    @Hidden
    @GetMapping("/login/{oauthServerType}")
    fun login(
        @PathVariable oauthServerType: OauthServerType,
        @RequestParam("code") code: String,
    ): ResponseEntity<AuthResponse> {
        val authTokenInfo = authService.login(oauthServerType, code)
        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo)
        val authResponse = AuthResponse(
            accessToken = authTokenInfo.accessToken
        )
        return ResponseEntity
            .ok()
            .header(SET_COOKIE, refreshTokenCookie.toString())
            .body(authResponse)
    }

    @Hidden
    @GetMapping("/token")
    fun extendLogin(
        @Auth authToken: AuthToken,
    ): ResponseEntity<AuthResponse> {
        val authTokenInfo = authService.extendLogin(authToken.accessToken, authToken.refreshToken)
        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo)
        val authResponse = AuthResponse(
            accessToken = authTokenInfo.accessToken
        )
        return ResponseEntity
            .ok()
            .header(SET_COOKIE, refreshTokenCookie.toString())
            .body(authResponse)
    }

    private fun createRefreshTokenCookie(authTokenInfo: AuthTokenInfo): ResponseCookie {
        return ResponseCookie.from("refresh-token", authTokenInfo.refreshToken)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .build()
    }
}
