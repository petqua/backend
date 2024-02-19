package com.petqua.presentation.member

import com.petqua.application.member.MemberService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Member", description = "회원 관련 API 명세")
@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@RequestMapping("/members")
@RestController
class MemberController(
    private val memberService: MemberService,
) {

    @Operation(summary = "회원 탈퇴 요청 API", description = "회원을 탈퇴합니다")
    @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    @DeleteMapping
    fun delete(
        @Auth loginMember: LoginMember
    ): ResponseEntity<Unit> {
        memberService.deleteBy(loginMember.memberId)
        return ResponseEntity.noContent().build()
    }
}
