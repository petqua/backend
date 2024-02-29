package com.petqua.application.payment

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.exception.payment.PaymentException
import com.petqua.exception.payment.PaymentExceptionType.UNAUTHORIZED_KEY
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.member
import com.petqua.test.fixture.order
import com.petqua.test.fixture.payOrderCommand
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigDecimal.ONE

@SpringBootTest(webEnvironment = NONE)
class PaymentFacadeServiceTest(
    private val paymentFacadeService: PaymentFacadeService,
    private val memberRepository: MemberRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: TossPaymentRepository,
    private val dataCleaner: DataCleaner,
    @SpykBean private val tossPaymentsApiClient: TossPaymentsApiClient,
) : BehaviorSpec({

    Given("주문을 결제할 때") {
        val member = memberRepository.save(member())
        val order = orderRepository.save(
            order(
                memberId = member.id,
                orderNumber = OrderNumber.from("orderNumber"),
                totalAmount = ONE
            )
        )

        When("유효한 요쳥이면") {
            paymentFacadeService.payOrder(
                command = payOrderCommand(
                    memberId = order.memberId,
                    orderNumber = order.orderNumber,
                    amount = order.totalAmount,
                )
            )

            Then("PG사에 결제 승인 요청을 보낸다") {
                verify(exactly = 1) {
                    tossPaymentsApiClient.confirmPayment(any(String::class), any(PaymentConfirmRequestToPG::class))
                }
            }
        }

        When("결제 승인에 성공하면") {
            paymentFacadeService.payOrder(
                command = payOrderCommand(
                    memberId = order.memberId,
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
                            memberId = order.memberId,
                            orderNumber = orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe ORDER_NOT_FOUND
            }
        }

        When("권한이 없는 회원이면") {
            val memberId = Long.MIN_VALUE

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.payOrder(
                        command = payOrderCommand(
                            memberId = memberId,
                            orderNumber = order.orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe OrderExceptionType.FORBIDDEN_ORDER
            }
        }

        When("주문의 총가격과 결제 금액이 다르면") {
            val amount = order.totalAmount + ONE

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.payOrder(
                        command = payOrderCommand(
                            memberId = order.memberId,
                            orderNumber = order.orderNumber,
                            amount = amount,
                        )
                    )
                }.exceptionType() shouldBe PAYMENT_PRICE_NOT_MATCH
            }
        }

        When("PG사와 통신할 때 예외가 발생하면") {
            every {
                tossPaymentsApiClient.confirmPayment(any(String::class), any(PaymentConfirmRequestToPG::class))
            } throws WebClientResponseException(
                404,
                "UNAUTHORIZED",
                null,
                "{\"code\":\"UNAUTHORIZED_KEY\",\"message\":\"인증되지 않은 시크릿 키 혹은 클라이언트 키 입니다.\",\"data\":null}".toByteArray(),
                null
            )

            Then("예외를 던진다") {
                shouldThrow<PaymentException> {
                    paymentFacadeService.payOrder(
                        command = payOrderCommand(
                            memberId = order.memberId,
                            orderNumber = order.orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe UNAUTHORIZED_KEY
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
