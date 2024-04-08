package com.petqua.presentation.member

import com.petqua.application.member.MemberService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.common.config.SIGN_UP_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.domain.auth.SignUpGuest
import com.petqua.presentation.member.dto.MemberAddProfileRequest
import com.petqua.presentation.member.dto.MemberSignUpRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Member", description = "회원 관련 API 명세")
@RequestMapping("/members")
@RestController
class MemberController(
    private val memberService: MemberService,
) {

    @Operation(summary = "회원가입 API", description = "약관 정보를 입력해 회원가입을 합니다")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @SecurityRequirement(name = SIGN_UP_TOKEN_SECURITY_SCHEME_KEY)
    @PostMapping("/sign-up")
    fun signUp(
        @Auth signUpGuest: SignUpGuest,
        @RequestBody request: MemberSignUpRequest,
    ): ResponseEntity<Unit> {
        val command = request.toCommand(signUpGuest.authCredentialsId)
        val authTokenInfo = memberService.signUp(command)

        val refreshTokenCookie = createRefreshTokenCookie(authTokenInfo.refreshToken)
        val headers = HttpHeaders().apply {
            set(AUTHORIZATION, authTokenInfo.accessToken)
            set(SET_COOKIE, refreshTokenCookie.toString())
        }
        return ResponseEntity.status(CREATED).headers(headers).build()
    }

    private fun createRefreshTokenCookie(refreshToken: String): ResponseCookie {
        return ResponseCookie.from("refresh-token", refreshToken)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .build()
    }

    @Operation(summary = "이름 검증 API", description = "회원의 닉네임, 수조 이름이 금지 단어를 포함하는지 검증합니다")
    @ApiResponse(responseCode = "200", description = "이름 검증 성공")
    @GetMapping("/validation/banned-word")
    fun validateContainingBannedWord(
        @RequestParam name: String,
    ): ResponseEntity<Unit> {
        memberService.validateContainingBannedWord(name)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "회원 물생활 프로필 입력 API", description = "회원의 추가적인 물생활 정보를 입력합니다")
    @ApiResponse(responseCode = "204", description = "회원 물생활 프로필 입력 성공")
    @SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
    @PostMapping("/profiles")
    fun addProfile(
        @Auth loginMember: LoginMember,
        @RequestBody request: MemberAddProfileRequest,
    ): ResponseEntity<Unit> {
        val command = request.toCommand(loginMember.memberId)
        memberService.addProfile(command)
        return ResponseEntity.noContent().build()
    }
}
