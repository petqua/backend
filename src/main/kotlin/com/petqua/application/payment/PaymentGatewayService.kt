package com.petqua.application.payment

import com.petqua.application.payment.infra.PaymentGatewayClient
import org.springframework.stereotype.Service

@Service
class PaymentGatewayService(
    private val paymentGatewayClient: PaymentGatewayClient,
) {

    fun confirmPayment(request: PaymentConfirmRequestToPG): PaymentResponseFromPG {
        return paymentGatewayClient.confirmPayment(request)
    }
}
