package com.petqua.domain.order

import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.test.fixture.order
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal.ONE
import java.math.BigDecimal.TEN

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
})
