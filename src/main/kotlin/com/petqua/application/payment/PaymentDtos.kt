package com.petqua.application.payment

import com.petqua.common.domain.Money
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.payment.tosspayment.TossPaymentType
import com.petqua.exception.payment.FailPaymentCode
import com.petqua.exception.payment.FailPaymentException
import com.petqua.exception.payment.FailPaymentExceptionType.ORDER_NUMBER_MISSING_EXCEPTION

data class SucceedPaymentCommand(
    val memberId: Long,
    val paymentType: TossPaymentType,
    val orderNumber: OrderNumber,
    val paymentKey: String,
    val amount: Money,
) {
    fun toPaymentConfirmRequest(): PaymentConfirmRequestToPG {
        return PaymentConfirmRequestToPG(
            orderNumber = orderNumber,
            paymentKey = paymentKey,
            amount = amount
        )
    }

    companion object {
        fun of(
            memberId: Long,
            paymentType: String,
            orderId: String,
            paymentKey: String,
            amount: Money,
        ): SucceedPaymentCommand {
            return SucceedPaymentCommand(
                memberId = memberId,
                paymentType = TossPaymentType.from(paymentType),
                orderNumber = OrderNumber.from(orderId),
                paymentKey = paymentKey,
                amount = amount,
            )
        }
    }
}

data class FailPaymentCommand(
    val memberId: Long,
    val code: FailPaymentCode,
    val message: String,
    val orderNumber: String?,
) {

    fun toOrderNumber(): OrderNumber {
        return orderNumber?.let { OrderNumber.from(it) } ?: throw FailPaymentException(ORDER_NUMBER_MISSING_EXCEPTION)
    }

    companion object {
        fun of(
            memberId: Long,
            code: String,
            message: String,
            orderId: String?,
        ): FailPaymentCommand {
            return FailPaymentCommand(
                memberId = memberId,
                code = FailPaymentCode.from(code),
                message = message,
                orderNumber = orderId,
            )
        }
    }
}

data class FailPaymentResponse(
    val code: FailPaymentCode,
    val message: String,
)
