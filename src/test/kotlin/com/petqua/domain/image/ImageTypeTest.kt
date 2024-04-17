package com.petqua.domain.image

import com.petqua.exception.image.ImageException
import com.petqua.exception.image.ImageExceptionType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ImageTypeTest : StringSpec({

    "contentType 으로 ImageType 을 얻는다" {
        val jpeg = ImageType.from("image/jpeg")

        jpeg.contentType shouldBe "image/jpeg"
        jpeg.extension shouldBe "jpeg"
    }

    "null을 입력하면 예외를 던진다" {
        shouldThrow<ImageException> {
            ImageType.from(null)
        }.exceptionType() shouldBe ImageExceptionType.INVALID_CONTENT_TYPE
    }

    "지원하지 않는 type을 입력하면 예외를 던진다" {
        shouldThrow<ImageException> {
            ImageType.from("image/gif")
        }.exceptionType() shouldBe ImageExceptionType.INVALID_CONTENT_TYPE
    }
})
