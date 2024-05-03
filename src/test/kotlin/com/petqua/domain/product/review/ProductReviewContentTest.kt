package com.petqua.domain.product.review

import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.REVIEW_CONTENT_LENGTH_OUT_OF_RANGE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class ProductReviewContentTest : StringSpec({

    "후기 작성 시 10자 미만, 300자 초과이면 예외를 던진다" {
        val shortContent = "후기".repeat(4)
        val longContent = "후기".repeat(151)

        listOf(shortContent, longContent).forAll { content ->
            shouldThrow<ProductReviewException> {
                ProductReviewContent(content)
            }.exceptionType() shouldBe REVIEW_CONTENT_LENGTH_OUT_OF_RANGE
        }
    }
})
