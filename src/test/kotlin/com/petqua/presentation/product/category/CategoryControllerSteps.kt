package com.petqua.presentation.product.category

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestReadSpecies(
    family: String,
): Response {
    return Given {
        log().all()
        param("family", family)
    } When {
        get("/categories")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
