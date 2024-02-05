package com.petqua.domain.product.dto

import com.petqua.common.util.throwExceptionWhen
import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.INVALID_SEARCH_WORD

const val PADDING_FOR_PAGING = 1
const val LIMIT_CEILING = 20

data class ProductReadCondition(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
    val word: String = "",
    val keyword: String = "",
) {

    companion object {
        fun toCondition(sourceType: ProductSourceType, sorter: Sorter): ProductReadCondition {
            return if (sourceType == ProductSourceType.HOME_NEW_ENROLLMENT) ProductReadCondition(
                sourceType,
                Sorter.ENROLLMENT_DATE_DESC
            )
            else ProductReadCondition(sourceType, sorter)
        }

        fun toSearchCondition(word: String): ProductReadCondition {
            throwExceptionWhen(word.isBlank()) { ProductException(INVALID_SEARCH_WORD) }
            return ProductReadCondition(word = word)
        }
    }
}

data class ProductPaging(
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    companion object {
        fun of(lastViewedId: Long?, limit: Int): ProductPaging {
            val adjustedLimit = if (limit > LIMIT_CEILING) LIMIT_CEILING else limit
            return ProductPaging(lastViewedId, adjustedLimit + PADDING_FOR_PAGING)
        }
    }
}

data class ProductResponse(
    val id: Long,
    val name: String,
    val category: String,
    val price: Int,
    val storeName: String,
    val discountRate: Int,
    val discountPrice: Int,
    val wishCount: Int,
    val reviewCount: Int,
    val reviewAverageScore: Double,
    val thumbnailUrl: String,
) {
    constructor(product: Product, storeName: String) : this(
        product.id,
        product.name,
        product.category,
        product.price.intValueExact(),
        storeName,
        product.discountRate,
        product.discountPrice.intValueExact(),
        product.wishCount.value,
        product.reviewCount,
        product.averageReviewScore(),
        product.thumbnailUrl,
    )
}
