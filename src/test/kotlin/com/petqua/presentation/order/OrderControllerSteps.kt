package com.petqua.presentation.order

import com.petqua.application.order.dto.SaveOrderResponse
import com.petqua.presentation.order.dto.OrderReadRequest
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

fun requestOrderAndReturnOrderNumber(
    request: SaveOrderRequest,
    accessToken: String,
): String {
    val response = Given {
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

    return response.`as`(SaveOrderResponse::class.java).orderNumber
}

fun requestReadOrderDetail(
    orderNumber: String,
    accessToken: String,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
            .queryParams("orderNumber", orderNumber)
    } When {
        get("/orders/detail")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadOrders(
    accessToken: String,
    request: OrderReadRequest,
): Response {
    return Given {
        log().all()
        auth().preemptive().oauth2(accessToken)
            .params(
                "lastViewedId", request.lastViewedId,
                "limit", request.limit,
                "lastViewedOrderNumber", request.lastViewedOrderNumber,
            )
    } When {
        get("/orders")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
