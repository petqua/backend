package com.petqua.presentation.member

import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@Tag(name = "Member", description = "회원 관련 API 명세")
@RequestMapping("/members")
@RestController
class MemberController {

    @Operation(summary = "회원 추가 정보 수집 API", description = "회원가입 시 추가적인 정보를 입력합니다")
    @ApiResponse(responseCode = "200", description = "어종 검색어 조회 성공")
    @PostMapping
    fun collectAdditionalProfile(
        @Auth loginMember: LoginMember,

        ): ResponseEntity<Unit> {
        return ResponseEntity.noContent().build()
    }
}
