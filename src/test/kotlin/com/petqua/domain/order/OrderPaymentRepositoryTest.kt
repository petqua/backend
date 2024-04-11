package com.petqua.domain.order

import com.petqua.exception.order.OrderPaymentException
import com.petqua.exception.order.OrderPaymentExceptionType.ORDER_PAYMENT_NOT_FOUND
import com.petqua.test.DataCleaner
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest(webEnvironment = NONE)
class OrderPaymentRepositoryTest(
    private val orderPaymentRepository: OrderPaymentRepository,
    private val dataCleaner: DataCleaner,
) : BehaviorSpec({

    Given("주문번호로 최신 OrderPayment 를 조회할 때") {
        val orderId = 1L
        orderPaymentRepository.saveAll(
            listOf(
                OrderPayment(
                    id = 1L,
                    orderId = orderId,
                    prevId = null,
                ),
                OrderPayment(
                    id = 2L,
                    orderId = orderId,
                    prevId = 1L,
                ),
                OrderPayment(
                    id = 3L,
                    orderId = orderId,
                    prevId = 2L,
                )
            )
        )

        When("주문번호를 입력하면") {
            val latestOrderPayment = orderPaymentRepository.findLatestByOrderIdOrThrow(orderId) {
                OrderPaymentException(ORDER_PAYMENT_NOT_FOUND)
            }

            Then("최신 OrderPayment 를 반환한다") {
                latestOrderPayment.id shouldBe 3L
                latestOrderPayment.prevId shouldBe 2L
                latestOrderPayment.orderId shouldBe orderId
            }
        }

        When("존재하지 않는 주문번호를 입력하면") {

            Then("예외를 던진다") {
                shouldThrow<OrderPaymentException> {
                    orderPaymentRepository.findLatestByOrderIdOrThrow(MIN_VALUE) {
                        OrderPaymentException(ORDER_PAYMENT_NOT_FOUND)
                    }
                }.exceptionType() shouldBe ORDER_PAYMENT_NOT_FOUND
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})

