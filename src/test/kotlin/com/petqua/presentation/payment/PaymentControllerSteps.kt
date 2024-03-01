package com.petqua.presentation.payment

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestPayOrder(
    accessToken: String,
    payOrderRequest: PayOrderRequest,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
        params(
            "paymentType", payOrderRequest.paymentType,
            "orderId", payOrderRequest.orderId,
            "paymentKey", payOrderRequest.paymentKey,
            "amount", payOrderRequest.amount
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
    val paramMap = mutableMapOf<String, Any?>().apply {
        put("code", failPaymentRequest.code)
        put("message", failPaymentRequest.message)
        put("orderId", failPaymentRequest.orderId)
    }.filterValues { it != null }

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
