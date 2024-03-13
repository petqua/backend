package com.petqua.presentation.payment

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.payment.FailPaymentResponse
import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.common.domain.findByIdOrThrow
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderPayment
import com.petqua.domain.order.OrderPaymentRepository
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.order.OrderStatus
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderExceptionType.FORBIDDEN_ORDER
import com.petqua.exception.order.OrderExceptionType.ORDER_CAN_NOT_PAY
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_ABORTED
import com.petqua.exception.payment.FailPaymentCode.PAY_PROCESS_CANCELED
import com.petqua.exception.payment.FailPaymentExceptionType.INVALID_CODE
import com.petqua.exception.payment.FailPaymentExceptionType.ORDER_NUMBER_MISSING_EXCEPTION
import com.petqua.exception.payment.PaymentExceptionType.UNAUTHORIZED_KEY
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.order
import com.petqua.test.fixture.succeedPaymentRequest
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigDecimal.ONE

class PaymentControllerTest(
    private val orderRepository: OrderRepository,
    private val paymentRepository: TossPaymentRepository,
    private val orderPaymentRepository: OrderPaymentRepository,
    @SpykBean private val tossPaymentsApiClient: TossPaymentsApiClient,
) : ApiTestConfig() {

    init {

        Given("결제 성공을 처리할 때") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            val order = orderRepository.save(
                order(
                    memberId = memberId,
                    orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                    totalAmount = ONE
                )
            )
            orderPaymentRepository.save(OrderPayment.from(order))

            When("유효한 요청이면") {
                val response = requestSucceedPayment(
                    accessToken = accessToken,
                    succeedPaymentRequest = succeedPaymentRequest(
                        orderId = order.orderNumber.value,
                        amount = order.totalAmount.value
                    )
                )

                Then("NO CONTENT 를 응답한다") {
                    response.statusCode shouldBe NO_CONTENT.value()
                }

                Then("Payment 객체를 저장한다") {
                    val payments = paymentRepository.findAll()

                    assertSoftly {
                        payments.size shouldBe 1
                        val payment = payments[0]

                        payment.orderNumber shouldBe order.orderNumber
                        payment.totalAmount shouldBe order.totalAmount
                    }
                }

                Then("Order의 상태를 변경한다") {
                    val updatedOrder = orderRepository.findByIdOrThrow(order.id)

                    assertSoftly {
                        updatedOrder.status shouldBe OrderStatus.PAYMENT_CONFIRMED
                    }
                }

                Then("OrderPayment 객체를 저장한다") {
                    val orderPayments = orderPaymentRepository.findAll()

                    assertSoftly {
                        orderPayments.size shouldBe 2
                        val payment = orderPayments[1]

                        payment.orderId shouldBe order.id
                        payment.tossPaymentId shouldBe paymentRepository.findAll()[0].id
                    }
                }
            }

            When("존재하지 않는 주문이면") {
                val orderNumber = "wrongOrderNumber"

                val response = requestSucceedPayment(
                    accessToken = accessToken,
                    succeedPaymentRequest = succeedPaymentRequest(
                        orderId = orderNumber,
                        amount = order.totalAmount.value
                    )
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe NOT_FOUND.value()
                        errorResponse.message shouldBe ORDER_NOT_FOUND.errorMessage()
                    }
                }
            }

            When("결제할 수 없는 주문이면") {
                val invalidOrder = orderRepository.save(
                    order(
                        memberId = memberId,
                        orderNumber = OrderNumber.from("202402211607021ORDERNUMBER"),
                        totalAmount = ONE,
                        status = OrderStatus.PAYMENT_CONFIRMED,
                    )
                )

                val response = requestSucceedPayment(
                    accessToken = accessToken,
                    succeedPaymentRequest = succeedPaymentRequest(
                        orderId = invalidOrder.orderNumber.value,
                        amount = invalidOrder.totalAmount.value
                    )
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe ORDER_CAN_NOT_PAY.errorMessage()
                    }
                }
            }

            When("권한이 없는 회원의 요청이면") {
                val otherAccessToken = signInAsMember().accessToken

                val response = requestSucceedPayment(
                    accessToken = otherAccessToken,
                    succeedPaymentRequest = succeedPaymentRequest(
                        orderId = order.orderNumber.value,
                        amount = order.totalAmount.value
                    )
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe FORBIDDEN.value()
                        errorResponse.message shouldBe FORBIDDEN_ORDER.errorMessage()
                    }
                }
            }

            When("주문의 총가격과 결제 금액이 다르면") {
                val wrongAmount = order.totalAmount + ONE

                val response = requestSucceedPayment(
                    accessToken = accessToken,
                    succeedPaymentRequest = succeedPaymentRequest(
                        orderId = order.orderNumber.value,
                        amount = wrongAmount.value
                    )
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe PAYMENT_PRICE_NOT_MATCH.errorMessage()
                    }
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

                val response = requestSucceedPayment(
                    accessToken = accessToken,
                    succeedPaymentRequest = succeedPaymentRequest(
                        orderId = order.orderNumber.value,
                        amount = order.totalAmount.value
                    )
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe UNAUTHORIZED.value()
                        errorResponse.message shouldBe UNAUTHORIZED_KEY.errorMessage()
                    }
                }
            }
        }

        Given("사용자가 취소한 결제 실패를 처리할 때") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            orderRepository.save(
                order(
                    memberId = memberId,
                    orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                    totalAmount = ONE
                )
            )

            When("유효한 실패 내역이 입력되면") {
                val response = requestFailPayment(
                    accessToken = accessToken,
                    failPaymentRequest = FailPaymentRequest(
                        code = PAY_PROCESS_CANCELED.name,
                        message = "사용자가 결제를 취소했습니다.",
                        orderId = null
                    )
                )

                Then("해당 내용을 응답한다") {
                    val failPaymentResponse = response.`as`(FailPaymentResponse::class.java)

                    assertSoftly(failPaymentResponse) {
                        response.statusCode shouldBe OK.value()
                        code shouldBe PAY_PROCESS_CANCELED
                        message shouldNotBe null
                    }
                }
            }

            When("유효하지 않은 실패 코드가 입력되면") {
                val response = requestFailPayment(
                    accessToken = accessToken,
                    failPaymentRequest = FailPaymentRequest(
                        code = "INVALID_CODE",
                        message = "사용자가 결제를 취소했습니다.",
                        orderId = null
                    )
                )

                Then("예외를 응답한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(exceptionResponse) {
                        response.statusCode shouldBe BAD_REQUEST.value()
                        exceptionResponse.message shouldBe INVALID_CODE.errorMessage()
                    }
                }
            }
        }

        Given("사용자가 취소하지 않은 결제 실패를 처리할 때") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            val order = orderRepository.save(
                order(
                    memberId = memberId,
                    orderNumber = OrderNumber.from("202402211607020ORDERNUMBER"),
                    totalAmount = ONE
                )
            )
            orderPaymentRepository.save(OrderPayment.from(order))

            When("유효한 실패 내역이 입력되면") {
                val response = requestFailPayment(
                    accessToken = accessToken,
                    failPaymentRequest = FailPaymentRequest(
                        code = PAY_PROCESS_ABORTED.name,
                        message = "시스템 오류로 결제가 실패했습니다.",
                        orderId = order.orderNumber.value,
                    )
                )

                Then("해당 내용을 응답한다") {
                    val failPaymentResponse = response.`as`(FailPaymentResponse::class.java)

                    assertSoftly(failPaymentResponse) {
                        response.statusCode shouldBe OK.value()
                        code shouldBe PAY_PROCESS_ABORTED
                        message shouldNotBe null
                    }
                }
            }

            When("주문번호가 입력되지 않으면") {
                val orderId = null

                val response = requestFailPayment(
                    accessToken = accessToken,
                    failPaymentRequest = FailPaymentRequest(
                        code = PAY_PROCESS_ABORTED.name,
                        message = "시스템 오류로 결제가 실패했습니다.",
                        orderId = orderId,
                    )
                )

                Then("예외를 응답한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(exceptionResponse) {
                        response.statusCode shouldBe BAD_REQUEST.value()
                        exceptionResponse.message shouldBe ORDER_NUMBER_MISSING_EXCEPTION.errorMessage()
                    }
                }
            }

            When("주문에 대한 권한이 없다면") {
                val otherAccessToken = signInAsMember().accessToken

                val response = requestFailPayment(
                    accessToken = otherAccessToken,
                    failPaymentRequest = FailPaymentRequest(
                        code = PAY_PROCESS_ABORTED.name,
                        message = "시스템 오류로 결제가 실패했습니다.",
                        orderId = order.orderNumber.value,
                    )
                )

                Then("예외를 응답한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(exceptionResponse) {
                        response.statusCode shouldBe FORBIDDEN.value()
                        exceptionResponse.message shouldBe FORBIDDEN_ORDER.errorMessage()
                    }
                }
            }

            When("존재하지 않는 주문이면") {
                val orderId = "wrongOrderId"

                val response = requestFailPayment(
                    accessToken = accessToken,
                    failPaymentRequest = FailPaymentRequest(
                        code = PAY_PROCESS_ABORTED.name,
                        message = "시스템 오류로 결제가 실패했습니다.",
                        orderId = orderId,
                    )
                )

                Then("예외를 응답한다") {
                    val exceptionResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(exceptionResponse) {
                        response.statusCode shouldBe NOT_FOUND.value()
                        exceptionResponse.message shouldBe ORDER_NOT_FOUND.errorMessage()
                    }
                }
            }
        }
    }
}
