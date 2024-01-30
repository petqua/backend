package com.petqua.presentation.cart

import com.petqua.presentation.cart.dto.SaveCartProductRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.HttpHeaders

fun requestSaveCartProduct(request: SaveCartProductRequest, accessToken: String): Response =
    Given {
        log().all()
            .body(request)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .contentType("application/json")
    } When {
        post("/carts")
    } Then {
        log().all()
    } Extract {
        response()
    }
