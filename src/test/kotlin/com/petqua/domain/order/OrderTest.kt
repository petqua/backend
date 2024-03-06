package com.petqua.domain.order

import com.petqua.domain.order.OrderStatus.CANCELED
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.FORBIDDEN_ORDER
import com.petqua.exception.order.OrderExceptionType.ORDER_CAN_NOT_PAY
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.test.fixture.order
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN
import kotlin.Long.Companion.MIN_VALUE

class OrderTest : StringSpec({

    "결제 금액을 검증한다" {
        val order = order(
            totalAmount = TEN
        )

        shouldNotThrow<OrderException> {
            order.validateAmount(TEN)
        }
    }

    "결제 금액을 검증할 때 금액이 다르다면 예외를 던진다" {
        val order = order(
            totalAmount = TEN
        )

        shouldThrow<OrderException> {
            order.validateAmount(ONE)
        }.exceptionType() shouldBe PAYMENT_PRICE_NOT_MATCH
    }

    "소유자를 검증한다" {
        val order = order(
            memberId = 1L
        )

        shouldNotThrow<OrderException> {
            order.validateOwner(1L)
        }
    }

    "소유자를 검증할 때 회원 Id가 다르다면 예외를 던진다" {
        val order = order(
            memberId = 1L
        )

        shouldThrow<OrderException> {
            order.validateOwner(MIN_VALUE)
        }.exceptionType() shouldBe FORBIDDEN_ORDER
    }

    "결제 처리를 한다" {
        val order = order(
            status = ORDER_CREATED
        )

        shouldNotThrow<OrderException> {
            order.pay()
        }
    }

    "결제 처리 시 결제를 할 수 없다면 예외를 던진다" {
        val order = order(
            status = CANCELED
        )

        shouldThrow<OrderException> {
            order.pay()
        }.exceptionType() shouldBe ORDER_CAN_NOT_PAY
    }
})
