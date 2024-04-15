package com.petqua.domain.image

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ImageTypeTest : StringSpec({

    "contentType 으로 ImageType 을 얻는다" {
        val jpeg = ImageType.from("image/jpeg")

        jpeg.contentType shouldBe "image/jpeg"
        jpeg.extension shouldBe "jpeg"
    }
})
