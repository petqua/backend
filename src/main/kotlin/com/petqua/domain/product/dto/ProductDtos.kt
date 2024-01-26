package com.petqua.domain.product.dto

import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter

const val PADDING_FOR_PAGING = 1
const val LIMIT_CEILING = 20

data class ProductReadCondition(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
) {

    companion object {
        fun of(sourceType: ProductSourceType, sorter: Sorter): ProductReadCondition {
            return if (sourceType == ProductSourceType.HOME_NEW_ENROLLMENT) ProductReadCondition(
                sourceType,
                Sorter.ENROLLMENT_DATE_DESC
            )
            else ProductReadCondition(sourceType, sorter)
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
        product.wishCount,
        product.reviewCount,
        product.averageReviewScore(),
        product.thumbnailUrl,
    )
}
