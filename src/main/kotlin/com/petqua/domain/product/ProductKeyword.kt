package com.petqua.domain.product

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ProductKeyword(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    val productId: Long = 0L,

    @Column(nullable = false)
    val word: String = "keyword",
) {

    init {
        throwExceptionWhen(word.isBlank()) { ProductException(ProductExceptionType.INVALID_SEARCH_WORD) }
    }
}
