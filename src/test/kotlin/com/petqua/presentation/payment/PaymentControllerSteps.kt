package com.petqua.presentation.payment

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

fun requestPayOrder(
    accessToken: String,
    payOrderRequest: PayOrderRequest,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
        contentType(APPLICATION_JSON_VALUE)
        body(payOrderRequest)
    } When {
        post("/orders/payment/success")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
