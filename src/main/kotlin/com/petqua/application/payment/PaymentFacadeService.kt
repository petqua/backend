package com.petqua.application.payment

import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_ABORTED
import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_CANCELED
import com.petqua.exception.payment.FailPaymentCode.REJECT_CARD_COMPANY
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentFacadeService(
    private val paymentService: PaymentService,
    private val paymentGatewayService: PaymentGatewayService,
) {

    private val log = LoggerFactory.getLogger(PaymentFacadeService::class.java)

    fun succeedPayment(command: SucceedPaymentCommand) {
        paymentService.validateAmount(command)
        val paymentResponse = paymentGatewayService.confirmPayment(command.toPaymentConfirmRequest())
        paymentService.processPayment(paymentResponse.toPayment())
    }

    fun failPayment(command: FailPaymentCommand): FailPaymentResponse {
        logFailPayment(command)
        if (command.code != PAY_PROCESS_CANCELED) {
            paymentService.cancelOrder(command.memberId, command.toOrderNumber())
        }
        return FailPaymentResponse(command.code, command.message)
    }

    private fun logFailPayment(command: FailPaymentCommand) {
        when (command.code) {
            PAY_PROCESS_ABORTED -> log.error("PG사에서 결제를 중단했습니다. message: ${command.message}, OrderNumber: ${command.orderNumber}, MemberId: ${command.memberId}")
            PAY_PROCESS_CANCELED -> log.warn("사용자가 결제를 중단했습니다. message: ${command.message}, OrderNumber: ${command.orderNumber}, MemberId: ${command.memberId}")
            REJECT_CARD_COMPANY -> log.warn("카드사에서 결제를 거절했습니다.. message: ${command.message}, OrderNumber: ${command.orderNumber}, MemberId: ${command.memberId}")
        }
    }
}
