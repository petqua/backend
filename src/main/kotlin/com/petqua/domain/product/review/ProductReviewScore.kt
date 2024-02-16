package com.petqua.domain.product.review

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.REVIEW_SCORE_OUT_OF_RANGE
import jakarta.persistence.Embeddable

@Embeddable
class ProductReviewScore(
    val value: Int,
) {

    init {
        throwExceptionWhen(value !in 1..5) { ProductReviewException(REVIEW_SCORE_OUT_OF_RANGE) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductReviewScore

        return value == other.value
    }

    override fun hashCode(): Int {
        return value
    }
}
