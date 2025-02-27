package com.petqua.domain.order

import com.petqua.domain.order.OrderStatus.ORDER_CONFIRMED
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.domain.order.OrderStatus.PAYMENT_CONFIRMED
import com.petqua.exception.order.OrderPaymentException
import com.petqua.exception.order.OrderPaymentExceptionType.ORDER_PAYMENT_NOT_FOUND
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.orderPayment
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlin.Long.Companion.MIN_VALUE
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

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

    Given("여러 주문번호로 가장 최신의 OrderPayment 조회 시") {
        val orderIds = listOf(1L, 2L, 3L)
        orderPaymentRepository.saveAll(
            listOf(
                orderPayment(orderId = 1L, prevId = 1L, status = ORDER_CREATED),
                orderPayment(orderId = 1L, prevId = 2L, status = ORDER_CONFIRMED),
                orderPayment(orderId = 2L, prevId = 3L, status = ORDER_CREATED),
                orderPayment(orderId = 2L, prevId = 4L, status = PAYMENT_CONFIRMED),
                orderPayment(orderId = 3L, prevId = 5L, status = ORDER_CREATED),
            )
        )

        When("주문번호를 입력하면") {
            val latestOrderPayments = orderPaymentRepository.findOrderStatusByOrderIds(orderIds)

            Then("최신 OrderPayment 를 반환한다") {
                latestOrderPayments.size shouldBe 3
                latestOrderPayments[0].orderId shouldBe 3L
                latestOrderPayments[0].status shouldBe ORDER_CREATED

                latestOrderPayments[1].orderId shouldBe 2L
                latestOrderPayments[1].status shouldBe PAYMENT_CONFIRMED
                
                latestOrderPayments[2].orderId shouldBe 1L
                latestOrderPayments[2].status shouldBe ORDER_CONFIRMED
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})

