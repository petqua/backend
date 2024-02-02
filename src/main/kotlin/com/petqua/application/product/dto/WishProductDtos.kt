package com.petqua.application.product.dto

import com.petqua.domain.product.WishProduct

data class SaveWishCommand(
    val memberId: Long,
    val productId: Long,
) {
    fun toWishProduct(): WishProduct {
        return WishProduct(
            memberId = memberId,
            productId = productId
        )
    }
}

data class DeleteWishCommand(
    val memberId: Long,
    val wishProductId: Long,
)
