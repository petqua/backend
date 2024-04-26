package com.petqua.application.image

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UrlConverterTest : StringSpec({

    "파일 경로와 파일이 저장된 url 을 입력해 접근 가능한 url로 변환한다" {
        val domainUrl = "https://domain.com"
        val filePath = "root/directory/image.jpeg"
        val storedUrl = "https://storedUrl.com/root/directory/image.jpeg"
        val urlConverter = UrlConverter(domainUrl)

        val accessibleUrl = urlConverter.convertToAccessibleUrl(filePath, storedUrl)

        accessibleUrl shouldBe "https://domain.com/root/directory/image.jpeg"
    }
})
