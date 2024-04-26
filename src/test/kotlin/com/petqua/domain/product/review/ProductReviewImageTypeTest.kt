package com.petqua.domain.product.review

import com.petqua.exception.product.review.ProductReviewException
import com.petqua.exception.product.review.ProductReviewExceptionType.UNSUPPORTED_IMAGE_TYPE
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ProductReviewImageTypeTest : StringSpec({

    "리뷰 이미지로 업로드할 수 있는 타입인지 검증한다" {
        shouldNotThrow<ProductReviewException> {
            ProductReviewImageType.validateSupported("image/jpeg")
        }
    }

    "리뷰 이미지로 업로드할 수 없는 타입이라면 예외를 던진다" {
        shouldThrow<ProductReviewException> {
            ProductReviewImageType.validateSupported("image/gif")
        }.exceptionType() shouldBe UNSUPPORTED_IMAGE_TYPE
    }
})
