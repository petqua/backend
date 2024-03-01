package com.petqua.application.payment

import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_CANCELED
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

    fun failPayment(command: FailPaymentCommand): FailPaymentResponse {
        if (command.code != PAY_PROCESS_CANCELED) {
            paymentService.cancelOrder(command.memberId, command.toOrderNumber())
        }
        return FailPaymentResponse(command.code, command.message)
    }
}
