package com.petqua.presentation.product

import com.petqua.common.domain.dto.DEFAULT_LAST_VIEWED_ID
import com.petqua.common.domain.dto.PAGING_LIMIT_CEILING
import com.petqua.domain.delivery.DeliveryMethod
import com.petqua.domain.product.ProductSourceType.NONE
import com.petqua.domain.product.Sorter
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification

fun requestReadProductById(
    productId: Long,
    accessToken: String? = null
): Response {
    return Given {
        log().all()
        authorize(accessToken)
        pathParam("productId", productId)
    } When {
        get("/products/{productId}")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

private fun RequestSpecification.authorize(accessToken: String?): RequestSpecification? {
    return accessToken?.let { auth().preemptive().oauth2(it) }
}

fun requestReadAllProducts(
    sourceType: String = NONE.name,
    sorter: String = Sorter.NONE.name,
    lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    limit: Int = PAGING_LIMIT_CEILING,
    accessToken: String? = null,
    ): Response {
    return Given {
        log().all()
        authorize(accessToken)
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
    word: String? = null,
    limit: Int = PAGING_LIMIT_CEILING,
): Response {
    return Given {
        val paramMap = mutableMapOf<String, Any?>().apply {
            put("word", word)
            put("limit", limit)
        }.filterValues { it != null }

        log().all()
        params(paramMap)
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
    deliveryMethod: DeliveryMethod = DeliveryMethod.NONE,
    sorter: String = Sorter.NONE.name,
    lastViewedId: Long = DEFAULT_LAST_VIEWED_ID,
    limit: Int = PAGING_LIMIT_CEILING,
    accessToken: String? = null
): Response {
    val paramMap = mutableMapOf<String, Any?>().apply {
        put("word", word)
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
        get("/products/search")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
