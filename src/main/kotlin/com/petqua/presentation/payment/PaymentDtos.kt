package com.petqua.presentation.payment

import com.petqua.application.payment.FailPaymentCommand
import com.petqua.application.payment.SucceedPaymentCommand
import java.math.BigDecimal

data class SucceedPaymentRequest(
    val paymentType: String,
    val orderId: String,
    val paymentKey: String,
    val amount: BigDecimal,
) {

    fun toCommand(memberId: Long): SucceedPaymentCommand {
        return SucceedPaymentCommand.of(
            memberId = memberId,
            paymentType = paymentType,
            orderId = orderId,
            paymentKey = paymentKey,
            amount = amount,
        )
    }
}

data class FailPaymentRequest(
    val code: String,
    val message: String,
    val orderId: String?,
) {

    fun toCommand(memberId: Long): FailPaymentCommand {
        return FailPaymentCommand.of(
            memberId = memberId,
            code = code,
            message = message,
            orderId = orderId,
        )
    }
}
