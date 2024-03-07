package com.petqua.presentation.fish

import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestReadAutoCompleteSpecies(
    word: String? = null,
    limit: Int? = null,
): Response {
    val paramMap = mapOf(
        "word" to word,
        "limit" to limit,
    ).filterValues { it != null }

    return Given {
        log().all()
        params(paramMap)
    } When {
        get("/fishes/species")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
