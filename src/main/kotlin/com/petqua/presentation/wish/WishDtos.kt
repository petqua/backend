package com.petqua.presentation.wish

import com.petqua.application.wish.SaveWishCommand
import com.petqua.domain.product.Product

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

data class WishResponse(
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
) {
    constructor(wishId: Long, product: Product, storeName: String) : this(
        wishId,
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
