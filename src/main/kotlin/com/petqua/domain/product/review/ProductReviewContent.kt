package com.petqua.domain.product.review

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.REVIEW_CONTENT_LENGTH_OUT_OF_RANGE
import jakarta.persistence.Embeddable

@Embeddable
class ProductReviewContent(
    val value: String,
) {

    init {
        throwExceptionWhen(value.length < MIN_LENGTH || value.length > MAX_LENGTH) {
            ProductReviewException(REVIEW_CONTENT_LENGTH_OUT_OF_RANGE)
        }
    }

    companion object {
        private const val MIN_LENGTH = 10
        private const val MAX_LENGTH = 300
    }
}
