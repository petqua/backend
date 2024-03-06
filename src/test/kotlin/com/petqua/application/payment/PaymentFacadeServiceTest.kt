package com.petqua.application.payment

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.domain.member.MemberRepository
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderStatus.CANCELED
import com.petqua.domain.order.OrderStatus.ORDER_CREATED
import com.petqua.domain.order.OrderStatus.PAYMENT_CONFIRMED
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderException
import com.petqua.exception.order.OrderExceptionType.FORBIDDEN_ORDER
import com.petqua.exception.order.OrderExceptionType.ORDER_CAN_NOT_PAY
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_ABORTED
import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_CANCELED
import com.petqua.exception.payment.FailPaymentException
import com.petqua.exception.payment.FailPaymentExceptionType.INVALID_CODE
import com.petqua.exception.payment.FailPaymentExceptionType.ORDER_NUMBER_MISSING_EXCEPTION
import com.petqua.exception.payment.PaymentException
import com.petqua.exception.payment.PaymentExceptionType.UNAUTHORIZED_KEY
import com.petqua.test.DataCleaner
import com.petqua.test.fixture.failPaymentCommand
import com.petqua.test.fixture.member
import com.petqua.test.fixture.order
import com.petqua.test.fixture.succeedPaymentCommand
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
import kotlin.Long.Companion.MIN_VALUE

@SpringBootTest(webEnvironment = NONE)
class PaymentFacadeServiceTest(
    private val paymentFacadeService: PaymentFacadeService,
    private val memberRepository: MemberRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: TossPaymentRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
    private val dataCleaner: DataCleaner,
    @SpykBean private val tossPaymentsApiClient: TossPaymentsApiClient,
) : BehaviorSpec({

    Given("결제 성공을 처리할 때") {
        val member = memberRepository.save(member())
        val order = orderRepository.save(
            order(
                memberId = member.id,
                orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                totalAmount = ONE
            )
        )

        When("유효한 요쳥이면") {
            paymentFacadeService.succeedPayment(
                command = succeedPaymentCommand(
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
            paymentFacadeService.succeedPayment(
                command = succeedPaymentCommand(
                    memberId = order.memberId,
                    orderNumber = order.orderNumber,
                    amount = order.totalAmount,
                )
            )

            Then("TossPayment 객체를 생성한다") {
                val payments = paymentRepository.findAll()

                assertSoftly {
                    payments.size shouldBe 1
                    val payment = payments[0]

                    payment.orderNumber shouldBe order.orderNumber
                    payment.totalAmount shouldBe order.totalAmount.setScale(2)
                }
            }

            Then("Order의 상태를 변경한다") {
                val updatedOrder = orderRepository.findByIdOrThrow(order.id)

                assertSoftly {
                    updatedOrder.status shouldBe PAYMENT_CONFIRMED
                }
            }

            Then("OrderPayment 객체를 생성한다") {
                val orderPayments = orderPaymentRepository.findAll()

                assertSoftly {
                    orderPayments.size shouldBe 1
                    val orderPayment = orderPayments[0]

                    orderPayment.orderId shouldBe order.id
                    orderPayment.tossPaymentId shouldBe paymentRepository.findAll()[0].id
                }
            }
        }

        When("존재하지 않는 주문이면") {
            val orderNumber = OrderNumber.from("20240221160702ORDERNUMBER0")

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.succeedPayment(
                        command = succeedPaymentCommand(
                            memberId = order.memberId,
                            orderNumber = orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe ORDER_NOT_FOUND
            }
        }

        When("결제할 수 없는 주문이면") {
            val invalidOrder = orderRepository.save(
                order(
                    memberId = member.id,
                    orderNumber = OrderNumber.from("202402211607021ORDERNUMBER"),
                    totalAmount = ONE,
                    status = PAYMENT_CONFIRMED,
                )
            )

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.succeedPayment(
                        command = succeedPaymentCommand(
                            memberId = invalidOrder.memberId,
                            orderNumber = invalidOrder.orderNumber,
                            amount = invalidOrder.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe ORDER_CAN_NOT_PAY
            }
        }

        When("권한이 없는 회원이면") {
            val memberId = MIN_VALUE

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.succeedPayment(
                        command = succeedPaymentCommand(
                            memberId = memberId,
                            orderNumber = order.orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe FORBIDDEN_ORDER
            }
        }

        When("주문의 총가격과 결제 금액이 다르면") {
            val amount = order.totalAmount + ONE

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.succeedPayment(
                        command = succeedPaymentCommand(
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
                    paymentFacadeService.succeedPayment(
                        command = succeedPaymentCommand(
                            memberId = order.memberId,
                            orderNumber = order.orderNumber,
                            amount = order.totalAmount,
                        )
                    )
                }.exceptionType() shouldBe UNAUTHORIZED_KEY
            }
        }
    }

    Given("사용자가 취소한 결제 실패를 처리할 때") {
        val member = memberRepository.save(member())
        val order = orderRepository.save(
            order(
                memberId = member.id,
                orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                totalAmount = ONE
            )
        )

        When("유효한 실패 내역이 입력되면") {
            val response = paymentFacadeService.failPayment(
                failPaymentCommand(
                    memberId = member.id,
                    code = PAY_PROCESS_CANCELED.name,
                    message = "사용자가 결제를 취소했습니다.",
                    orderNumber = null
                )
            )

            Then("실패 내역을 응답한다") {
                response shouldBe FailPaymentResponse(
                    code = PAY_PROCESS_CANCELED,
                    message = "사용자가 결제를 취소했습니다.",
                )
            }

            Then("주문을 취소하지 않는다") {
                val updatedOrder = orderRepository.findByIdOrThrow(order.id)

                updatedOrder.status shouldBe ORDER_CREATED
            }
        }

        When("유효하지 않은 실패 코드가 입력되면") {
            val code = "INVALID_CODE"

            Then("예외를 던진다") {
                shouldThrow<FailPaymentException> {
                    paymentFacadeService.failPayment(
                        failPaymentCommand(
                            memberId = member.id,
                            code = code,
                            message = "사용자가 결제를 취소했습니다.",
                            orderNumber = null
                        )
                    )
                }.exceptionType() shouldBe INVALID_CODE
            }
        }
    }

    Given("사용자가 취소하지 않은 결제 실패를 처리할 때") {
        val member = memberRepository.save(member())
        val order = orderRepository.save(
            order(
                memberId = member.id,
                orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                totalAmount = ONE
            )
        )

        When("유효한 실패 내역이 입력되면") {
            val response = paymentFacadeService.failPayment(
                failPaymentCommand(
                    memberId = member.id,
                    code = PAY_PROCESS_ABORTED.name,
                    message = "시스템 오류로 결제가 실패했습니다.",
                    orderNumber = order.orderNumber.value,
                )
            )

            Then("해당 내용을 응답한다") {
                response shouldBe FailPaymentResponse(
                    code = PAY_PROCESS_ABORTED,
                    message = "시스템 오류로 결제가 실패했습니다.",
                )
            }

            Then("주문을 취소한다") {
                val updatedOrder = orderRepository.findByIdOrThrow(order.id)

                updatedOrder.status shouldBe CANCELED
            }
        }

        When("주문번호가 입력되지 않으면") {
            val orderNumber = null

            Then("예외를 던진다") {
                shouldThrow<FailPaymentException> {
                    paymentFacadeService.failPayment(
                        failPaymentCommand(
                            memberId = member.id,
                            code = PAY_PROCESS_ABORTED.name,
                            message = "시스템 오류로 결제가 실패했습니다.",
                            orderNumber = orderNumber,
                        )
                    )
                }.exceptionType() shouldBe ORDER_NUMBER_MISSING_EXCEPTION
            }
        }

        When("주문에 대한 권한이 없다면") {
            val memberId = MIN_VALUE

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.failPayment(
                        failPaymentCommand(
                            memberId = memberId,
                            code = PAY_PROCESS_ABORTED.name,
                            message = "시스템 오류로 결제가 실패했습니다.",
                            orderNumber = order.orderNumber.value,
                        )
                    )
                }.exceptionType() shouldBe FORBIDDEN_ORDER
            }
        }

        When("존재하지 않는 주문이면") {
            val orderNumber = "wrongOrderNumber"

            Then("예외를 던진다") {
                shouldThrow<OrderException> {
                    paymentFacadeService.failPayment(
                        failPaymentCommand(
                            memberId = member.id,
                            code = PAY_PROCESS_ABORTED.name,
                            message = "시스템 오류로 결제가 실패했습니다.",
                            orderNumber = orderNumber,
                        )
                    )
                }.exceptionType() shouldBe ORDER_NOT_FOUND
            }
        }
    }

    afterContainer {
        dataCleaner.clean()
    }
})
