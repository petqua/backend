package com.petqua.domain.order

import com.petqua.domain.order.OrderStatus.CANCELED
import com.petqua.domain.order.OrderStatus.PAYMENT_CONFIRMED
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType
import com.petqua.test.fixture.orderPayment
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OrderPaymentTest : StringSpec({

    "결제를 진행한다" {
        val orderPayment = orderPayment()
        val payedOrderPayment = orderPayment.pay(tossPaymentId = 1L)

        assertSoftly(payedOrderPayment) {
            orderId shouldBe orderPayment.orderId
            tossPaymentId shouldBe 1L
            status shouldBe PAYMENT_CONFIRMED
            prevId shouldBe orderPayment.id
        }
    }

    "결제 시도시 결제가 가능한 상태가 아니라면 예외를 던진다" {
        val orderPayment = orderPayment(
            status = PAYMENT_CONFIRMED
        )

        shouldThrow<OrderException> {
            orderPayment.pay(tossPaymentId = 1L)
        }.exceptionType() shouldBe OrderExceptionType.ORDER_CAN_NOT_PAY
    }

    "주문을 취소한다" {
        val orderPayment = orderPayment()
        val payedOrderPayment = orderPayment.cancel()

        assertSoftly(payedOrderPayment) {
            orderId shouldBe orderPayment.orderId
            tossPaymentId shouldBe orderPayment.tossPaymentId
            status shouldBe CANCELED
            prevId shouldBe orderPayment.id
        }
    }
})
