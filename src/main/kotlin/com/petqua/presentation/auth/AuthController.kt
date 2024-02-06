package com.petqua.presentation.auth

import com.petqua.application.auth.AuthService
import com.petqua.application.auth.AuthTokenInfo
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.token.AuthToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
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

@Tag(name = "Auth", description = "인증 관련 API 명세")
@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @Operation(summary = "리다이렉트 요청 API", description = "Oauth 로그인 페이지로 리다이렉트합니다")
    @ApiResponse(responseCode = "302", description = "리다이렉트 성공")
    @GetMapping("/{oauthServerType}")
    fun redirectToAuthCodeRequestUrl(
        @Schema(description = "Oauth 서버", example = "KAKAO")
        @PathVariable oauthServerType: OauthServerType,
    ): ResponseEntity<Void> {
        val redirectUri = authService.getAuthCodeRequestUrl(oauthServerType)
        return ResponseEntity.status(FOUND)
            .location(redirectUri)
            .build()
    }

    @Operation(summary = "소셜 로그인 API", description = "소셜 로그인을 합니다")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @GetMapping("/login/{oauthServerType}")
    fun login(
        @Schema(description = "Oauth 서버", example = "KAKAO")
        @PathVariable oauthServerType: OauthServerType,

        @Schema(description = "auth code")
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

    @Operation(summary = "로그인 유지 API", description = "refresh token으로 access token을 재발급 받습니다")
    @ApiResponse(responseCode = "200", description = "재발급 성공")
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
