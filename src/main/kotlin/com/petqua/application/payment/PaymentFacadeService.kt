package com.petqua.application.payment

import com.petqua.application.order.dto.PayOrderCommand
import org.springframework.stereotype.Service

@Service
class PaymentFacadeService(
    private val paymentService: PaymentService,
    private val paymentGatewayService: PaymentGatewayService,
) {

    fun payOrder(command: PayOrderCommand) {
        paymentService.validateAmount(command)
        val paymentResponse = paymentGatewayService.confirmPayment(command.toPaymentConfirmRequest())
        paymentService.save(paymentResponse.toPayment())
    }
}
