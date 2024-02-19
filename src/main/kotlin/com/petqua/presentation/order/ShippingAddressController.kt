package com.petqua.presentation.order

import com.petqua.application.order.ShippingAddressService
import com.petqua.application.order.dto.ReadDefaultShippingAddressResponse
import com.petqua.application.order.dto.SaveShippingAddressResponse
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.order.dto.SaveShippingAddressRequest
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

@Tag(name = "ShippingAddress", description = "배송지 관련 API 명세")
@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@RequestMapping("/shipping-address")
@RestController
class ShippingAddressController(
    private val shippingAddressService: ShippingAddressService,
) {

    @Operation(summary = "배송지 생성 API", description = "배송지를 생성합니다")
    @ApiResponse(responseCode = "200", description = "배송지 생성 성공")
    @PostMapping
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveShippingAddressRequest,
    ): ResponseEntity<SaveShippingAddressResponse> {
        val response = shippingAddressService.save(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "기본 배송지 조회 API", description = "멤버의 기본 배송지를 조회합니다")
    @ApiResponse(responseCode = "200", description = "기본 배송지 조회 성공")
    @GetMapping("/default")
    fun readDefaultShippingAddress(
        @Auth loginMember: LoginMember,
    ): ResponseEntity<ReadDefaultShippingAddressResponse?> {
        val response = shippingAddressService.readDefaultShippingAddress(loginMember.memberId)
        return ResponseEntity.ok(response)
    }
}
