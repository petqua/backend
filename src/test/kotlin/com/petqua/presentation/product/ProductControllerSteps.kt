package com.petqua.presentation.product

import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.LIMIT_CEILING
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.HttpHeaders

fun requestReadProductById(
    productId: Long,
    accessToken: String
): Response {
    return Given {
        log().all()
        header(HttpHeaders.AUTHORIZATION, accessToken)
        pathParam("productId", productId)
    } When {
        get("/products/{productId}")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadAllProducts(
    sourceType: String = NONE.name,
    sorter: String = Sorter.NONE.name,
    lastViewedId: Long? = null,
    limit: Int = LIMIT_CEILING,
    accessToken: String
): Response {
    return Given {
        log().all()
        header(HttpHeaders.AUTHORIZATION, accessToken)
        params(
            "sourceType", sourceType,
            "sorter", sorter,
            "lastViewedId", lastViewedId,
            "limit", limit
        )
    } When {
        get("/products")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadProductKeyword(
    word: String = "",
    limit: Int = LIMIT_CEILING,
    accessToken: String
): Response {
    return Given {
        log().all()
        header(HttpHeaders.AUTHORIZATION, accessToken)
        params(
            "word", word,
            "limit", limit
        )
    } When {
        get("/products/keywords")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadProductBySearch(
    word: String = "",
    lastViewedId: Long? = null,
    limit: Int = LIMIT_CEILING,
    accessToken: String
): Response {
    return Given {
        log().all()
        header(HttpHeaders.AUTHORIZATION, accessToken)
        params(
            "word", word,
            "lastViewedId", lastViewedId,
            "limit", limit
        )
    } When {
        get("/products/search")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
