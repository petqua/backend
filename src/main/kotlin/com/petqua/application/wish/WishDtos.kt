package com.petqua.application.wish

import com.petqua.domain.wish.Wish

data class SaveWishCommand(
    val memberId: Long,
    val productId: Long,
) {
    fun toWish(): Wish {
        return Wish(
            memberId = memberId,
            productId = productId
        )
    }
}

data class DeleteWishCommand(
    val memberId: Long,
    val wishId: Long,
)
