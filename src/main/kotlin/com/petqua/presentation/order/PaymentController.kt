package com.petqua.presentation.order

import com.petqua.application.payment.PaymentService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import com.petqua.presentation.order.dto.PayOrderRequest
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@Tag(name = "Payment", description = "결제 관련 API 명세")
@RequestMapping("/orders/payment")
@RestController
class PaymentController(
    private val paymentService: PaymentService,
) {

    @PostMapping("/success")
    fun payOrder(
        @Auth loginMember: LoginMember,
        @RequestBody request: PayOrderRequest,
    ): ResponseEntity<Unit> {
        paymentService.payOrder(request.toCommand())
        return ResponseEntity.ok().build()
    }

    @PostMapping("/fail")
    fun cancelOrderPayment(
        @Auth loginMember: LoginMember,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }
}
