package com.petqua.presentation.order

import com.petqua.presentation.order.dto.SaveShippingAddressRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestSaveShippingAddress(
    request: SaveShippingAddressRequest,
    accessToken: String,
): Response {
    return Given {
        log().all()
            .body(request)
            .auth().preemptive().oauth2(accessToken)
            .contentType("application/json")
    } When {
        post("/shipping-address")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
