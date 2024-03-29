package com.petqua.test.fixture

import com.petqua.application.payment.FailPaymentCommand
import com.petqua.application.payment.SucceedPaymentCommand
import com.petqua.common.domain.Money
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.payment.tosspayment.TossPaymentType
import com.petqua.domain.payment.tosspayment.TossPaymentType.NORMAL
import com.petqua.presentation.payment.SucceedPaymentRequest
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO

fun succeedPaymentCommand(
    memberId: Long = 0L,
    paymentType: TossPaymentType = NORMAL,
    orderNumber: OrderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
    paymentKey: String = "paymentKey",
    amount: BigDecimal = ZERO,
): SucceedPaymentCommand {
    return SucceedPaymentCommand(
        memberId = memberId,
        paymentType = paymentType,
        orderNumber = orderNumber,
        paymentKey = paymentKey,
        amount = Money.from(amount),
    )
}

fun succeedPaymentRequest(
    paymentType: String = NORMAL.name,
    orderId: String = "orderId",
    paymentKey: String = "paymentKey",
    amount: BigDecimal = ONE,
): SucceedPaymentRequest {
    return SucceedPaymentRequest(
        paymentType = paymentType,
        orderId = orderId,
        paymentKey = paymentKey,
        amount = Money.from(amount),
    )
}

fun failPaymentCommand(
    memberId: Long,
    code: String,
    message: String,
    orderNumber: String?,
): FailPaymentCommand {
    return FailPaymentCommand.of(
        memberId = memberId,
        code = code,
        message = message,
        orderId = orderNumber,
    )
}
