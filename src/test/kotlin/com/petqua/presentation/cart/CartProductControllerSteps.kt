package com.petqua.presentation.cart

import com.petqua.presentation.cart.dto.SaveCartProductRequest
import com.petqua.presentation.cart.dto.UpdateCartProductOptionRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.HttpHeaders

fun requestSaveCartProduct(
    request: SaveCartProductRequest,
    accessToken: String
): Response {
    return Given {
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
}

fun saveCartProductAndReturnId(accessToken: String): Long {
    val sampleCartProduct = SaveCartProductRequest(
        productId = 1L,
        quantity = 1,
        isMale = true,
        deliveryMethod = "COMMON"
    )
    val response = requestSaveCartProduct(sampleCartProduct, accessToken)
    return parseCartProductIdFromLocationHeader(response)
}

private fun parseCartProductIdFromLocationHeader(response: Response): Long {
    return response.header(HttpHeaders.LOCATION).split("/").last().toLong()
}


fun requestUpdateCartProductOption(
    cartProductId: Long,
    request: UpdateCartProductOptionRequest,
    accessToken: String
): Response {
    return Given {
        log().all()
            .body(request)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .contentType("application/json")
    } When {
        patch("/carts/items/{cartProductId}/options", cartProductId)
    } Then {
        log().all()
    } Extract {
        response()
    }
}
