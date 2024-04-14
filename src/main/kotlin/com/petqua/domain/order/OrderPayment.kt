package com.petqua.domain.order

import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.domain.order.OrderStatus.PAYMENT_CONFIRMED
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class OrderPayment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val orderId: Long,

    val tossPaymentId: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus = ORDER_CREATED,

    @Column(unique = true)
    val prevId: Long? = null,
) {

    companion object {
        fun from(order: Order): OrderPayment {
            return OrderPayment(
                orderId = order.id,
            )
        }
    }

    fun cancel(): OrderPayment {
        // TODO isAbleToCancel 사용
        // throwExceptionWhen(!isAbleToCancel) {
        //     OrderException(ORDER_NOT_FOUND)
        // }
        return OrderPayment(
            orderId = orderId,
            tossPaymentId = tossPaymentId,
            status = OrderStatus.CANCELED,
            prevId = id,
        )
    }

    fun pay(tossPaymentId: Long): OrderPayment {
        throwExceptionWhen(!status.isAbleToPay()) {
            throw OrderException(OrderExceptionType.ORDER_CAN_NOT_PAY)
        }
        return OrderPayment(
            orderId = orderId,
            tossPaymentId = tossPaymentId,
            status = PAYMENT_CONFIRMED,
            prevId = id,
        )
    }
}
