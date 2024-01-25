package com.petqua.dto

import com.petqua.domain.Product
import com.petqua.domain.ProductSourceType
import com.petqua.domain.ProductSourceType.HOME_NEW_ENROLLMENT
import com.petqua.domain.Sorter
import com.petqua.domain.Sorter.ENROLLMENT_DATE_DESC
import com.petqua.domain.Sorter.NONE

private const val PADDING_FOR_PAGING = 1

data class ProductReadRequest(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = NONE,
    val lastViewedId: Long? = null,
    val limit: Int = 20,
) {
    fun toReadConditions(): ProductReadCondition {
        return ProductReadCondition.of(sourceType, sorter)
    }

    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
}

data class ProductReadCondition(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = NONE,
) {

    companion object {
        fun of(sourceType: ProductSourceType, sorter: Sorter): ProductReadCondition {
            return if (sourceType == HOME_NEW_ENROLLMENT) ProductReadCondition(sourceType, ENROLLMENT_DATE_DESC)
            else ProductReadCondition(sourceType, sorter)
        }
    }
}

data class ProductPaging(
    val lastViewedId: Long? = null,
    val limit: Int = 20,
) {
    companion object {
        fun of(lastViewedId: Long?, limit: Int): ProductPaging {
            return ProductPaging(lastViewedId, limit + PADDING_FOR_PAGING)
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

data class ProductsResponse(
    val products: List<ProductResponse>,
    val hasNextPage: Boolean,
    val totalProductsCount: Int,
) {
    companion object {
        fun of(products: List<ProductResponse>, limit: Int, totalProductsCount: Int): ProductsResponse {
            return if (products.size > limit) {
                ProductsResponse(products.dropLast(PADDING_FOR_PAGING), hasNextPage = true, totalProductsCount)
            } else {
                ProductsResponse(products, hasNextPage = false, totalProductsCount)
            }
        }
    }
}

data class ProductCountResponse(
    val count: Int,
)

data class ProductDetailResponse(
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
    val description: String,
) {
    constructor(product: Product, storeName: String, reviewAverageScore: Double) : this(
        product.id,
        product.name,
        product.category,
        product.price.intValueExact(),
        storeName,
        product.discountRate,
        product.discountPrice.intValueExact(),
        product.wishCount,
        product.reviewCount,
        reviewAverageScore,
        product.thumbnailUrl,
        product.description,
    )
}
