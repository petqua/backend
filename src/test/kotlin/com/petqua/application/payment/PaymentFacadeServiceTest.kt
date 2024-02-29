package com.petqua.application.payment

import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.test.DataCleaner
import com.petqua.test.config.ApiClientTestConfig
import com.petqua.test.fake.FakeTossPaymentsApiClient
import com.petqua.test.fixture.order
import com.petqua.test.fixture.payOrderCommand
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.math.BigDecimal.ONE

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(ApiClientTestConfig::class)
class PaymentFacadeServiceTest(
    private val paymentFacadeService: PaymentFacadeService,
    private val orderRepository: OrderRepository,
    private val paymentRepository: TossPaymentRepository,
    private val dataCleaner: DataCleaner,

    private val fakeTossPaymentsApiClient: FakeTossPaymentsApiClient,
) : BehaviorSpec({

    Given("결제를 요청할 때") {
        val order = orderRepository.save(
            order(
                orderNumber = OrderNumber.from("orderNumber"),
                totalAmount = ONE
            )
        )

//        When("유효한 요쳥이면") {
//            every {
//                fakeTossPaymentsApiClient.confirmPayment(any(String::class), any(PaymentConfirmRequestToPG::class))
//            } returns paymentResponseFromPG(
//                paymentKey = "paymentKey",
//                orderNumber = order.orderNumber,
//                totalAmount = order.totalAmount
//            )
//
//            Then("PG사에 결제 승인 요청을 보낸다") {
//                verify(exactly = 1) {
//                    fakeTossPaymentsApiClient.confirmPayment(any(String::class), any(PaymentConfirmRequestToPG::class))
//                }
//            }
//        }

        When("결제 승인에 성공하면") {
            paymentFacadeService.payOrder(
                command = payOrderCommand(
                    orderNumber = order.orderNumber,
                    amount = order.totalAmount,
                )
            )

            Then("Payment 객체를 생성한다") {
                val payments = paymentRepository.findAll()

                assertSoftly {
                    payments.size shouldBe 1
                    val payment = payments[0]

                    payment.orderNumber shouldBe order.orderNumber
                    payment.totalAmount shouldBe order.totalAmount.setScale(2)
                }
            }
        }

        When("존재하지 않는 주문이면") {
            val orderNumber = OrderNumber.from("wrongOrderNumber")

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.payOrder(
                        command = payOrderCommand(
                            orderNumber = orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe ORDER_NOT_FOUND
            }
        }

        When("결제 금액이 다르면") {
            val amount = order.totalAmount + ONE

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.payOrder(
                        command = payOrderCommand(
                            orderNumber = order.orderNumber,
                            amount = amount,
                        )
                    )
                }.exceptionType() shouldBe PAYMENT_PRICE_NOT_MATCH
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
