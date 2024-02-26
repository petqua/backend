package com.petqua.presentation.cart

import com.petqua.application.cart.CartProductService
import com.petqua.application.cart.dto.CartProductWithSupportedOptionResponse
import com.petqua.application.cart.dto.DeleteCartProductCommand
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.cart.dto.SaveCartProductRequest
import com.petqua.presentation.cart.dto.UpdateCartProductOptionRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@Tag(name = "Cart", description = "봉달(장바구니) 관련 API 명세")
@RequestMapping("/carts")
@RestController
class CartProductController(
    private val cartProductService: CartProductService,
) {

    @Operation(summary = "봉달 추가 API", description = "상품을 봉달에 추가합니다")
    @ApiResponse(responseCode = "201", description = "봉달 추가 성공")
    @PostMapping
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveCartProductRequest
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId)
        val cartProductId = cartProductService.save(command)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/items/{id}")
            .buildAndExpand(cartProductId)
            .toUri()
        return ResponseEntity.created(location).build()
    }

    @Operation(summary = "봉달 옵션 수정 API", description = "봉달에 있는 상품의 옵션을 변경합니다")
    @ApiResponse(responseCode = "204", description = "봉달 상품 옵션 변경 성공")
    @PatchMapping("/{cartProductId}/options")
    fun updateOptions(
        @Auth loginMember: LoginMember,
        @PathVariable cartProductId: Long,
        @RequestBody request: UpdateCartProductOptionRequest
    ): ResponseEntity<Void> {
        val command = request.toCommand(loginMember.memberId, cartProductId)
        cartProductService.updateOptions(command)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "봉달 삭제 API", description = "상품을 봉달에서 삭제합니다")
    @ApiResponse(responseCode = "204", description = "봉달 삭제 성공")
    @DeleteMapping("/{cartProductId}")
    fun delete(
        @Auth loginMember: LoginMember,
        @Schema(description = "봉달 상품 id", example = "1")
        @PathVariable cartProductId: Long
    ): ResponseEntity<Void> {
        val command = DeleteCartProductCommand(
            memberId = loginMember.memberId,
            cartProductId = cartProductId
        )
        cartProductService.delete(command)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "봉달 목록 조회 API", description = "봉달 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "봉달 목록 조회 성공")
    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
    ): ResponseEntity<List<CartProductWithSupportedOptionResponse>> {
        val responses = cartProductService.readAll(loginMember.memberId)
        return ResponseEntity.ok(responses)
    }
}
