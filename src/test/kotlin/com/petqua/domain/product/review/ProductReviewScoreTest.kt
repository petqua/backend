package com.petqua.domain.product.review

import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ProductReviewScoreTest : StringSpec({

    "ProductReviewScore 생성시 별점이 1점부터 5점 사이가 아니면 예외가 발생한다." {
        listOf(0, 6, 10, -1, 100)
            .forAll { score ->
                shouldThrow<ProductReviewException> {
                    ProductReviewScore(value = score)
                }.exceptionType() shouldBe ProductReviewExceptionType.REVIEW_SCORE_OUT_OF_RANGE
            }
    }
})
