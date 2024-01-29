package com.petqua.presentation.cart.dto

import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.domain.cart.DeliveryMethod

data class SaveCartProductRequest(
    val productId: Long,
    val quantity: Int,
    val isMale: Boolean,
    val deliveryMethod: String,
) {

    fun toCommand(memberId: Long): SaveCartProductCommand {
        return SaveCartProductCommand(
            memberId = memberId,
            productId = productId,
            quantity = quantity,
            isMale = isMale,
            deliveryMethod = DeliveryMethod.valueOf(deliveryMethod)
        )
    }
}
