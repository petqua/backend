package com.petqua.presentation.payment

import com.ninjasquad.springmockk.SpykBean
import com.petqua.application.payment.PaymentConfirmRequestToPG
import com.petqua.application.payment.infra.TossPaymentsApiClient
import com.petqua.common.exception.ExceptionResponse
import com.petqua.domain.order.OrderNumber
import com.petqua.domain.order.OrderRepository
import com.petqua.domain.payment.tosspayment.TossPaymentRepository
import com.petqua.exception.order.OrderExceptionType.FORBIDDEN_ORDER
import com.petqua.exception.order.OrderExceptionType.ORDER_NOT_FOUND
import com.petqua.exception.order.OrderExceptionType.PAYMENT_PRICE_NOT_MATCH
import com.petqua.exception.payment.PaymentExceptionType.UNAUTHORIZED_KEY
import com.petqua.test.ApiTestConfig
import com.petqua.test.fixture.order
import com.petqua.test.fixture.payOrderRequest
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.math.BigDecimal.ONE

class PaymentControllerTest(
    private val orderRepository: OrderRepository,
    private val paymentRepository: TossPaymentRepository,
    @SpykBean private val tossPaymentsApiClient: TossPaymentsApiClient,
) : ApiTestConfig() {

    init {

        Given("주문을 결제할 때") {
            val accessToken = signInAsMember().accessToken
            val memberId = getMemberIdByAccessToken(accessToken)

            val order = orderRepository.save(
                order(
                    memberId = memberId,
                    orderNumber = OrderNumber.from("orderNumber"),
                    totalAmount = ONE
                )
            )

            When("유효한 요청이면") {
                val response = requestPayOrder(
                    accessToken = accessToken,
                    payOrderRequest = payOrderRequest(
                        orderId = order.orderNumber.value,
                        amount = order.totalAmount
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
                        payment.totalAmount shouldBe order.totalAmount.setScale(2)
                    }
                }
            }

            When("존재하지 않는 주문이면") {
                val orderNumber = "wrongOrderNumber"

                val response = requestPayOrder(
                    accessToken = accessToken,
                    payOrderRequest = payOrderRequest(
                        orderId = orderNumber,
                        amount = order.totalAmount
                    )
                )

                Then("예외를 응답한다") {
                    val errorResponse = response.`as`(ExceptionResponse::class.java)

                    assertSoftly(response) {
                        statusCode shouldBe BAD_REQUEST.value()
                        errorResponse.message shouldBe ORDER_NOT_FOUND.errorMessage()
                    }
                }
            }

            When("권한이 없는 회원의 요청이면") {
                val otherAccessToken = signInAsMember().accessToken

                val response = requestPayOrder(
                    accessToken = otherAccessToken,
                    payOrderRequest = payOrderRequest(
                        orderId = order.orderNumber.value,
                        amount = order.totalAmount
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

                val response = requestPayOrder(
                    accessToken = accessToken,
                    payOrderRequest = payOrderRequest(
                        orderId = order.orderNumber.value,
                        amount = wrongAmount
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

                val response = requestPayOrder(
                    accessToken = accessToken,
                    payOrderRequest = payOrderRequest(
                        orderId = order.orderNumber.value,
                        amount = order.totalAmount
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
    }
}
