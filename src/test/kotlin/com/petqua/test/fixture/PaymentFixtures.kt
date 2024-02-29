package com.petqua.test.fixture

import com.petqua.application.order.dto.PayOrderCommand
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.payment.tosspayment.TossPaymentType
import com.petqua.domain.payment.tosspayment.TossPaymentType.NORMAL
import com.petqua.presentation.payment.PayOrderRequest
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO

fun payOrderCommand(
    memberId: Long = 0L,
    paymentType: TossPaymentType = NORMAL,
    orderNumber: OrderNumber = OrderNumber.from("OrderNumber"),
    paymentKey: String = "paymentKey",
    amount: BigDecimal = ZERO,
): PayOrderCommand {
    return PayOrderCommand(
        memberId = memberId,
        paymentType = paymentType,
        orderNumber = orderNumber,
        paymentKey = paymentKey,
        amount = amount,
    )
}

fun payOrderRequest(
    paymentType: String = NORMAL.name,
    orderId: String = "orderId",
    paymentKey: String = "paymentKey",
    amount: BigDecimal = ONE,
): PayOrderRequest {
    return PayOrderRequest(
        paymentType = paymentType,
        orderId = orderId,
        paymentKey = paymentKey,
        amount = amount,
    )
}
