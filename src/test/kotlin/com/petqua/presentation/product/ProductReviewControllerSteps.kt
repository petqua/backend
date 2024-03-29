package com.petqua.presentation.product

import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import com.petqua.presentation.product.dto.UpdateReviewRecommendationRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.MediaType


fun requestReadAllReviewProducts(
    productId: Long,
    sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    lastViewedId: Long = -1,
    limit: Int = 20,
    score: Int? = null,
    photoOnly: Boolean = false
): Response {
    return Given {
        log().all()
        params(
            "sorter", sorter.name,
            "lastViewedId", lastViewedId,
            "limit", limit,
            "score", score,
            "photoOnly", photoOnly
        )
        pathParam("productId", productId)
    } When {
        get("/products/{productId}/reviews")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadProductReviewCount(productId: Long): Response {
    return Given {
        log().all()
    } When {
        get("/products/{productId}/review-statistics", productId)
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestUpdateReviewRecommendation(
    request: UpdateReviewRecommendationRequest,
    accessToken: String
): Response {
    return Given {
        log().all()
            .body(request)
            .auth().preemptive().oauth2(accessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
    } When {
        post("/product-reviews/recommendation")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
