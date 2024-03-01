package com.petqua.application.payment

import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_ABORTED
import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_CANCELED
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentFacadeService(
    private val paymentService: PaymentService,
    private val paymentGatewayService: PaymentGatewayService,
) {

    private val log = LoggerFactory.getLogger(PaymentFacadeService::class.java)

    fun payOrder(command: PayOrderCommand) {
        paymentService.validateAmount(command)
        val paymentResponse = paymentGatewayService.confirmPayment(command.toPaymentConfirmRequest())
        paymentService.save(paymentResponse.toPayment())
    }

    fun failPayment(command: FailPaymentCommand): FailPaymentResponse {
        if (command.code == PAY_PROCESS_ABORTED) {
            log.error("PG사에서 결제가 중단되었습니다. message: ${command.message}, OrderNumber: ${command.orderNumber}, MemberId: ${command.memberId}")
        }

        if (command.code != PAY_PROCESS_CANCELED) {
            paymentService.cancelOrder(command.memberId, command.toOrderNumber())
        }
        return FailPaymentResponse(command.code, command.message)
    }
}
