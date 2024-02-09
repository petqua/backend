package com.petqua.presentation.product.category

import com.petqua.domain.product.Sorter
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
    species: String? = null,
    canDeliverSafely: Boolean? = null,
    canDeliverCommonly: Boolean? = null,
    canPickUp: Boolean? = null,
    sorter: Sorter? = null,
    lastViewedId: Long? = null,
    limit: Int? = null,
): Response {
    val paramMap = mutableMapOf<String, Any?>().apply {
        put("family", family)
        put("species", species)
        put("canDeliverSafely", canDeliverSafely)
        put("canDeliverCommonly", canDeliverCommonly)
        put("canPickUp", canPickUp)
        put("sorter", sorter)
        put("lastViewedId", lastViewedId)
        put("limit", limit)
    }.filterValues { it != null }

    return Given {
        log().all()
        params(paramMap)
    } When {
        get("/categories/products")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
