package com.petqua.presentation.auth

import com.petqua.application.auth.AuthService
import com.petqua.domain.auth.oauth.OauthServerType
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.FOUND
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @GetMapping("/{oauthServerType}")
    fun redirectToAuthCodeRequestUrl(
        @PathVariable oauthServerType: OauthServerType,
    ): ResponseEntity<Void> {
        val redirectUri = authService.getAuthCodeRequestUrl(oauthServerType)
        return ResponseEntity.status(FOUND)
            .location(redirectUri)
            .build()
    }

    @GetMapping("/login/{oauthServerType}")
    fun login(
        @PathVariable oauthServerType: OauthServerType,
        @RequestParam("code") code: String,
    ): ResponseEntity<AuthResponse> {
        val authTokenInfo = authService.login(oauthServerType, code)
        val refreshTokenCookie = ResponseCookie.from("refresh-token", authTokenInfo.refreshToken)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .build()
        val authResponse = AuthResponse(
            accessToken = authTokenInfo.accessToken
        )
        return ResponseEntity
            .ok()
            .header(SET_COOKIE, refreshTokenCookie.toString())
            .body(authResponse)
    }
}
