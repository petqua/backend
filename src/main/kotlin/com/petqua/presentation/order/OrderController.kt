package com.petqua.presentation.order

import com.petqua.application.order.OrderService
import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.order.dto.SaveOrderRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/orders")
@RestController
class OrderController(
    private val orderService: OrderService,
) {

    @PostMapping
    fun save(
        @Auth loginMember: LoginMember,
        @RequestBody request: SaveOrderRequest,
    ): ResponseEntity<SaveOrderResponse> {
        val response = orderService.save(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(response)
    }
}
