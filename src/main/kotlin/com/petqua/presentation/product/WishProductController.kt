package com.petqua.presentation.product

import com.petqua.application.product.WishProductService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.product.dto.ReadAllWishProductRequest
import com.petqua.presentation.product.dto.UpdateWishRequest
import com.petqua.presentation.product.dto.WishProductsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@Tag(name = "WishProduct", description = "찜 관련 API 명세")
@RequestMapping("/products/wishes")
@RestController
class WishProductController(
    private val wishProductService: WishProductService
) {

    @Operation(summary = "찜 상태 변경 API", description = "찜 상태를 변경합니다")
    @ApiResponse(responseCode = "204", description = "찜 상태 변경 성공")
    @PostMapping
    fun update(
        @Auth loginMember: LoginMember,
        @RequestBody request: UpdateWishRequest
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId)
        wishProductService.update(command)
        return ResponseEntity
            .noContent()
            .build()
    }

    @Operation(summary = "찜 상품 목록 조회 API", description = "찜한 상품 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "찜 상품 목록 조회 성공")
    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
        request: ReadAllWishProductRequest,
    ): ResponseEntity<WishProductsResponse> {
        val responses = wishProductService.readAll(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(responses)
    }
}
