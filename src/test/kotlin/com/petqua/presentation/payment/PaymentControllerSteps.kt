package com.petqua.presentation.payment

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestSucceedPayment(
    accessToken: String,
    succeedPaymentRequest: SucceedPaymentRequest,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
        params(
            "paymentType", succeedPaymentRequest.paymentType,
            "orderId", succeedPaymentRequest.orderId,
            "paymentKey", succeedPaymentRequest.paymentKey,
            "amount", succeedPaymentRequest.amount
        )
    } When {
        post("/orders/payment/success")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestFailPayment(
    accessToken: String,
    failPaymentRequest: FailPaymentRequest,
): Response {
    val paramMap = mapOf(
        "code" to failPaymentRequest.code,
        "message" to failPaymentRequest.message,
        "orderId" to failPaymentRequest.orderId,
    ).filterValues { it != null }

    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
        params(paramMap)
    } When {
        post("/orders/payment/fail")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
