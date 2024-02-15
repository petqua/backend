package com.petqua.presentation.product.category

import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.Sorter
import com.petqua.test.authorize
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestReadSpecies(
    family: String? = null,
): Response {
    val paramMap = mutableMapOf<String, Any?>().apply {
        put("family", family)
    }.filterValues { it != null }

    return Given {
        log().all()
        params(paramMap)
    } When {
        get("/categories")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadProducts(
    family: String? = null,
    species: List<String> = listOf(),
    deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,
    sorter: Sorter? = null,
    lastViewedId: Long? = null,
    limit: Int? = null,
    accessToken: String? = null,
): Response {
    val paramMap = mutableMapOf<String, Any?>().apply {
        put("family", family)
        put("species", species.takeIf { it.isNotEmpty() })
        put("deliveryMethod", deliveryMethod.name)
        put("sorter", sorter)
        put("lastViewedId", lastViewedId)
        put("limit", limit)
    }.filterValues { it != null }

    return Given {
        log().all()
        authorize(accessToken)
        params(paramMap)
    } When {
        get("/categories/products")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
