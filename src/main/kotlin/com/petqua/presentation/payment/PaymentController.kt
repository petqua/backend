package com.petqua.presentation.payment

import com.petqua.application.payment.FailPaymentResponse
import com.petqua.application.payment.PaymentFacadeService
import com.petqua.common.config.ACCESS_TOKEN_SECURITY_SCHEME_KEY
import com.petqua.domain.auth.Auth
import com.petqua.domain.auth.LoginMember
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SecurityRequirement(name = ACCESS_TOKEN_SECURITY_SCHEME_KEY)
@Tag(name = "Payment", description = "결제 관련 API 명세")
@RequestMapping("/orders/payment")
@RestController
class PaymentController(
    private val paymentFacadeService: PaymentFacadeService,
) {

    @PostMapping("/success")
    fun payOrder(
        @Auth loginMember: LoginMember,
        request: PayOrderRequest,
    ): ResponseEntity<Unit> {
        paymentFacadeService.payOrder(request.toCommand(loginMember.memberId))
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/fail")
    fun failPayment(
        @Auth loginMember: LoginMember,
        request: FailPaymentRequest,
    ): ResponseEntity<FailPaymentResponse> {
        val response = paymentFacadeService.failPayment(request.toCommand(loginMember.memberId))
        return ResponseEntity.ok(response)
    }
}
