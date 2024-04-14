package com.petqua.presentation.order

import com.petqua.presentation.order.dto.SaveOrderRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

fun requestSaveOrder(
    request: SaveOrderRequest,
    accessToken: String,
): Response {
    return Given {
        log().all()
        body(request)
        auth().preemptive().oauth2(accessToken)
        contentType(APPLICATION_JSON_VALUE)
    } When {
        post("/orders")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
