package com.petqua.presentation.auth

import com.petqua.application.auth.AuthFacadeService
import com.petqua.application.auth.AuthTokenInfo
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.oauth.OauthServerType
import com.petqua.domain.auth.token.AuthToken
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 관련 API 명세")
@RequestMapping("/auth")
@RestController
class AuthController(
    private val authFacadeService: AuthFacadeService
) {

    @Operation(summary = "리다이렉트 요청 API", description = "Oauth 로그인 페이지로 리다이렉트하는 URI를 조회합니다")
    @ApiResponse(responseCode = "200", description = "리다이렉트 성공")
    @GetMapping("/{oauthServerType}")
    fun redirectToAuthCodeRequestUrl(
        @Schema(description = "Oauth 서버", example = "KAKAO")
        @PathVariable oauthServerType: OauthServerType,
    ): ResponseEntity<RedirectUriResponse> {
        val redirectUri = authFacadeService.getAuthCodeRequestUrl(oauthServerType)
        return ResponseEntity
            .ok()
            .body(RedirectUriResponse(redirectUri.toString()))
    }

    @Operation(summary = "소셜 로그인 API(테스트 불가)", description = "소셜 로그인을 합니다")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @GetMapping("/login/{oauthServerType}")
    fun login(
        @Schema(description = "Oauth 서버", example = "KAKAO")
        @PathVariable oauthServerType: OauthServerType,

        @Schema(description = "auth code")
        @RequestParam("code") code: String,
    ): ResponseEntity<Unit> {
        val authTokenInfo = authFacadeService.login(oauthServerType, code)
        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo)
        val headers = HttpHeaders().apply {
            set(AUTHORIZATION, authTokenInfo.accessToken)
            set(SET_COOKIE, refreshTokenCookie.toString())
        }
        return ResponseEntity
            .ok()
            .headers(headers)
            .build()
    }

    @Operation(
        summary = "로그인 유지 API(테스트 불가)",
        description = "refresh token으로 access token을 재발급 받습니다",
        parameters = [
            Parameter(
                name = "Authorization",
                `in` = ParameterIn.HEADER,
                schema = Schema(type = "string"),
                description = "'Bearer 'prefix를 붙여서 AccessToken 을 입력해주세요.",
                example = "Bearer xxx.yyy.zzz",
            ),
            Parameter(
                name = "refresh-token",
                `in` = ParameterIn.COOKIE,
                schema = Schema(type = "string"),
                description = "RefreshToken 을 입력해주세요.",
                example = "xxx.yyy.zzz",
            )
        ]
    )
    @ApiResponse(responseCode = "200", description = "재발급 성공")
    @GetMapping("/token")
    fun extendLogin(
        @Parameter(hidden = true) @Auth authToken: AuthToken,
    ): ResponseEntity<Unit> {
        val authTokenInfo = authFacadeService.extendLogin(authToken.accessToken, authToken.refreshToken)
        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo)
        val headers = HttpHeaders().apply {
            set(AUTHORIZATION, authTokenInfo.accessToken)
            set(SET_COOKIE, refreshTokenCookie.toString())
        }
        return ResponseEntity
            .ok()
            .headers(headers)
            .build()
    }

    private fun createRefreshTokenCookie(authTokenInfo: AuthTokenInfo): ResponseCookie {
        return ResponseCookie.from("refresh-token", authTokenInfo.refreshToken)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .build()
    }

    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴를 합니다")
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @DeleteMapping
    fun delete(
        @Auth loginMember: LoginMember
    ): ResponseEntity<Unit> {
        authFacadeService.deleteBy(loginMember.memberId)
        return ResponseEntity.noContent().build()
    }
}
