package com.petqua.presentation.product

import com.petqua.application.product.dto.ReadAllWishProductCommand
import com.petqua.application.product.dto.SaveWishCommand
import com.petqua.domain.product.Product
import com.petqua.domain.product.dto.LIMIT_CEILING
import com.petqua.domain.product.dto.PADDING_FOR_PAGING

data class SaveWishRequest(
    val productId: Long,
) {

    fun toCommand(memberId: Long): SaveWishCommand {
        return SaveWishCommand(
            memberId = memberId,
            productId = productId
        )
    }
}

data class ReadAllWishProductRequest(
    val lastViewedId: Long? = null,
    val limit: Int = LIMIT_CEILING,
) {

    fun toCommand(memberId: Long): ReadAllWishProductCommand {
        return ReadAllWishProductCommand(
            memberId = memberId,
            lastViewedId = lastViewedId,
            limit = limit
        )
    }
}

data class WishProductsResponse(
    val wishProducts: List<WishProductResponse>,
    val hasNextPage: Boolean,
    val totalWishProductsCount: Int,
) {
    companion object {
        fun of(wishProducts: List<WishProductResponse>, limit: Int, totalWishProductsCount: Int): WishProductsResponse {
            return if (wishProducts.size > limit) {
                WishProductsResponse(
                    wishProducts.dropLast(PADDING_FOR_PAGING),
                    hasNextPage = true,
                    totalWishProductsCount
                )
            } else {
                WishProductsResponse(wishProducts, hasNextPage = false, totalWishProductsCount)
            }
        }
    }
}

data class WishProductResponse(
    val id: Long,
    val productId: Long,
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
    val isDeletedProduct: Boolean,
) {
    constructor(wishProductId: Long, product: Product, storeName: String) : this(
        wishProductId,
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
        product.isDeleted,
    )
}
