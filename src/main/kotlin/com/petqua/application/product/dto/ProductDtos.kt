package com.petqua.application.product.dto

import com.petqua.domain.product.Product
import com.petqua.domain.product.ProductKeyword
import com.petqua.domain.product.ProductSourceType
import com.petqua.domain.product.Sorter
import com.petqua.domain.product.dto.LIMIT_CEILING
import com.petqua.domain.product.dto.PADDING_FOR_PAGING
import com.petqua.domain.product.dto.ProductPaging
import com.petqua.domain.product.dto.ProductReadCondition
import com.petqua.domain.product.dto.ProductResponse

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

data class ProductReadCommand(
    val sourceType: ProductSourceType = ProductSourceType.NONE,
    val sorter: Sorter = Sorter.NONE,
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {
    fun toReadConditions(): ProductReadCondition {
        return ProductReadCondition.toCondition(sourceType, sorter)
    }

    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
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

data class ProductSearchCommand(
    val word: String = "",
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toSearchCondition(): ProductReadCondition {
        return ProductReadCondition.toSearchCondition(word)
    }

    fun toPaging(): ProductPaging {
        return ProductPaging.of(lastViewedId, limit)
    }
}

data class ProductKeywordCommand(
    val word: String = "",
    val limit: Int = LIMIT_CEILING,
) {

    fun toProductKeyword(): ProductKeyword {
        return ProductKeyword(word = word)
    }
}

data class ProductKeywordResponse(
    val keyword: String,
)
