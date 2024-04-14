package com.petqua.test.fixture

import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderStatus
import com.petqua.domain.order.OrderStatus.ORDER_CREATED

fun orderPayment(
    id: Long = 0L,
    orderId: Long = 0L,
    tossPaymentId: Long = 0L,
    status: OrderStatus = ORDER_CREATED,
    prevId: Long = 0L,
): OrderPayment {
    return OrderPayment(
        id = id,
        orderId = orderId,
        tossPaymentId = tossPaymentId,
        status = status,
        prevId = prevId,
    )
}
