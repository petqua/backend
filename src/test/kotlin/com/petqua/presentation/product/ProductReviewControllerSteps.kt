package com.petqua.presentation.product

import com.petqua.domain.product.review.ProductReviewSorter
import com.petqua.domain.product.review.ProductReviewSorter.REVIEW_DATE_DESC
import com.petqua.presentation.product.dto.CreateReviewRequest
import com.petqua.presentation.product.dto.UpdateReviewRecommendationRequest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE

fun requestCreateProductReview(
    accessToken: String,
    productId: Long,
    request: CreateReviewRequest,
): Response {
    val requestGivenSpec = Given {
        log().all()
        contentType(MULTIPART_FORM_DATA_VALUE)
        auth().preemptive().oauth2(accessToken)
        pathParam("productId", productId)
        multiPart("score", request.score)
        multiPart("content", request.content)
    }

    request.images.forEach { image ->
        requestGivenSpec.multiPart("images", image.name, image.bytes, image.contentType)
    }

    return requestGivenSpec When {
        post("/products/{productId}/reviews")
    } Then {
        log().all()
    } Extract {
        response()
    }
}

fun requestReadAllReviewProducts(
    productId: Long,
    sorter: ProductReviewSorter = REVIEW_DATE_DESC,
    lastViewedId: Long = -1,
    limit: Int = 20,
    score: Int? = null,
    photoOnly: Boolean = false,
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
    accessToken: String,
): Response {
    return Given {
        log().all()
            .body(request)
            .auth().preemptive().oauth2(accessToken)
            .contentType(APPLICATION_JSON_VALUE)
    } When {
        post("/product-reviews/recommendation")
    } Then {
        log().all()
    } Extract {
        response()
    }
}
