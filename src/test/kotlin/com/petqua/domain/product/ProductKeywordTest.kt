package com.petqua.domain.product

import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_SEARCH_WORD
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ProductKeywordTest : StringSpec({
    "상품 키워드 생성" {
        val word = "keyword"

        shouldNotThrow<ProductException> { ProductKeyword(word = word) }
    }

    "상품 키워드 빈 값일 경우 생성 실패" {
        val emptyWord = ""

        shouldThrow<ProductException> {
            ProductKeyword(word = emptyWord)
        }.exceptionType() shouldBe INVALID_SEARCH_WORD
    }
})
