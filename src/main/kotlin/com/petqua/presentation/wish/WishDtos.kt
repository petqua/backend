package com.petqua.presentation.wish

import com.petqua.application.wish.SaveWishCommand

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
