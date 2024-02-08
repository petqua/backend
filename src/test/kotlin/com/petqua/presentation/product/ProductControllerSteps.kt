package com.petqua.presentation.product

import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.PRODUCT_LIMIT_CEILING
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response

fun requestReadProductById(
    productId: Long,
    accessToken: String
): Response {
    return Given {
        log().all()
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
    limit: Int = PRODUCT_LIMIT_CEILING,
    accessToken: String
): Response {
    return Given {
        log().all()
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
    limit: Int = PRODUCT_LIMIT_CEILING,
    accessToken: String
): Response {
    return Given {
        log().all()
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
    limit: Int = PRODUCT_LIMIT_CEILING,
    accessToken: String
): Response {
    return Given {
        log().all()
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
