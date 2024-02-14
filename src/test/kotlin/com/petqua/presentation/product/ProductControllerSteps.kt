package com.petqua.presentation.product

import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter
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
    lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    limit: Int = PAGING_LIMIT_CEILING,
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
    limit: Int = PAGING_LIMIT_CEILING,
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
    word: String? = null,
    canDeliverSafely: Boolean? = null,
    canDeliverCommonly: Boolean? = null,
    canPickUp: Boolean? = null,
    sorter: String = Sorter.NONE.name,
    lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    limit: Int = PAGING_LIMIT_CEILING,
    accessToken: String
): Response {
    val paramMap = mutableMapOf<String, Any?>().apply {
        put("word", word)
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
        get("/products/search")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
