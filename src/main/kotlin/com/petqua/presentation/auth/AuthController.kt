package com.petqua.presentation.auth

import com.petqua.application.auth.AuthFacadeService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.oauth.OauthServerType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 관련 API 명세")
@RequestMapping("/auth")
@RestController
class AuthController(
    private val authFacadeService: AuthFacadeService,
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
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공, 회원가입이 필요 없는 경우"
            ),
            ApiResponse(
                responseCode = "201",
                description = "로그인 성공, 회원가입이 필요한 경우"
            )
        ]
    )
    @GetMapping("/login/{oauthServerType}")
    fun login(
        @Schema(description = "Oauth 서버", example = "KAKAO")
        @PathVariable oauthServerType: OauthServerType,

        @Schema(description = "auth code")
        @RequestParam("code") code: String,
    ): ResponseEntity<Any> {
        val authTokenInfo = authFacadeService.login(oauthServerType, code)

        if (authTokenInfo.isSignUpNeeded()) {
            return ResponseEntity.status(CREATED).body(SignUpTokenResponse(authTokenInfo.signUpToken))
        }

        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo.refreshToken)
        val headers = HttpHeaders().apply {
            set(AUTHORIZATION, authTokenInfo.accessToken)
            set(SET_COOKIE, refreshTokenCookie.toString())
        }
        return ResponseEntity.ok().headers(headers).build()
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
        @Parameter(hidden = true) @Auth loginToken: LoginTokenRequest,
    ): ResponseEntity<Unit> {
        val authTokenInfo = authFacadeService.extendLogin(loginToken.accessToken, loginToken.refreshToken)
        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo.refreshToken)
        val headers = HttpHeaders().apply {
            set(AUTHORIZATION, authTokenInfo.accessToken)
            set(SET_COOKIE, refreshTokenCookie.toString())
        }
        return ResponseEntity
            .ok()
            .headers(headers)
            .build()
    }

    private fun createRefreshTokenCookie(refreshToken: String): ResponseCookie {
        return ResponseCookie.from("refresh-token", refreshToken)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .build()
    }

    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴를 합니다")
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @DeleteMapping("/members")
    fun delete(
        @Auth loginMember: LoginMember,
    ): ResponseEntity<Unit> {
        authFacadeService.deleteBy(loginMember.memberId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 합니다")
    @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @PatchMapping("/members/sign-out")
    fun logOut(
        @Parameter(hidden = true) @Auth loginToken: LoginTokenRequest,
    ): ResponseEntity<Unit> {
        authFacadeService.logOut(loginToken.accessToken, loginToken.refreshToken)
        return ResponseEntity.noContent().build()
    }
}
