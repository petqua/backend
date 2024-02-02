package com.petqua.application.wish

import com.petqua.domain.wish.WishProduct

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
