package com.petqua.domain.order

import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class OrderNumberTest : StringSpec({

    "주문번호 생성 시 주문번호 형식에 맞는지 검증한다" {
        val orderNumber = OrderNumber.generate().value

        shouldNotThrow<OrderException> {
            OrderNumber.from(orderNumber)
        }
    }

    "주문번호 생성 시 주문번호 형식에 맞지 않다면 예외를 던진다" {
        val invalidOrderNumber = "2024030416391INVALID"

        shouldThrow<OrderException> {
            OrderNumber.from(invalidOrderNumber)
        }.exceptionType() shouldBe ORDER_NOT_FOUND
    }
})
