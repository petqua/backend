package com.petqua.application

import com.petqua.domain.Product

data class ProductReadRequest(
        val sourceType: ProductSourceType,
        val sorter: Sorter,
        val lastViewedId: Long,
        val limit: Int,
)

data class ProductResponse(
        val id: Long,
        val name: String,
        val storeName: String,
        val price: Int,
        val discountRate: Int,
        val discountPrice: Int,
        val wishCount: Int,
        val reviewCount: Int,
        val reviewAverageScore: Double,
        val thumbnailUrl: String,
)

data class ProductsResponse(
        val products: List<ProductResponse>,
        val hasNextPage: Boolean,
)

data class ProductCountResponse(
        val count: Int,
)

data class ProductDetailResponse(
        val id: Long,
        val name: String,
        val storeName: String,
        val price: Int,
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
            storeName,
            product.price.intValueExact(),
            product.discountRate,
            product.discountPrice.intValueExact(),
            product.wishCount,
            product.reviewCount,
            reviewAverageScore,
            product.thumbnailUrl,
            product.description,
    )
}
