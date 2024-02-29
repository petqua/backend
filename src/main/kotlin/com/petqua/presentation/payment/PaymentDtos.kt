package com.petqua.presentation.payment

import com.petqua.application.order.dto.PayOrderCommand
import java.math.BigDecimal

data class PayOrderRequest(
    val paymentType: String,
    val orderId: String,
    val paymentKey: String,
    val amount: BigDecimal,
) {

    fun toCommand(memberId: Long): PayOrderCommand {
        return PayOrderCommand.of(
            memberId = memberId,
            paymentType = paymentType,
            orderId = orderId,
            paymentKey = paymentKey,
            amount = amount,
        )
    }
}
