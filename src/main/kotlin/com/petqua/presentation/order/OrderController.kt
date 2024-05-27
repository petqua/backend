package com.petqua.presentation.order

import com.petqua.application.order.OrderService
import com.petqua.application.order.dto.OrderDetailReadQuery
import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.order.dto.OrderDetailResponse
import com.petqua.presentation.order.dto.OrderReadRequest
import com.petqua.presentation.order.dto.OrdersResponse
import com.petqua.presentation.order.dto.SaveOrderRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@Tag(name = "Order", description = "주문 관련 API 명세")
@RequestMapping("/orders")
@RestController
class OrderController(
    private val orderService: OrderService,
) {

    @Operation(summary = "주문 생성 API", description = "주문을 생성합니다")
    @ApiResponse(responseCode = "200", description = "주문 생성 성공")
    @PostMapping
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveOrderRequest,
    ): ResponseEntity<SaveOrderResponse> {
        val response = orderService.save(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "주문 상세 조회 API", description = "주문 상세를 조회합니다")
    @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공")
    @GetMapping("/detail")
    fun readDetail(
        @Auth loginMember: LoginMember,
        @RequestParam orderNumber: String,
    ): ResponseEntity<OrderDetailResponse> {
        val query = OrderDetailReadQuery.of(loginMember.memberId, orderNumber)
        val response = orderService.readDetail(query)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "주문 내역 조회 API", description = "주문 내역을 조회합니다")
    @ApiResponse(responseCode = "200", description = "주문 내역 조회 성공")
    @GetMapping
    fun readAll(
        @Auth loginMember: LoginMember,
        request: OrderReadRequest,
    ): ResponseEntity<OrdersResponse> {
        val query = request.toQuery(loginMember)
        val response = orderService.readAll(query)
        return ResponseEntity.ok(response)
    }
}
