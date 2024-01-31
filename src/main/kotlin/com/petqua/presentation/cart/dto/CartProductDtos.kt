package com.petqua.presentation.cart.dto

import com.petqua.application.cart.dto.DeleteCartProductCommand
import com.petqua.application.cart.dto.SaveCartProductCommand
import com.petqua.application.cart.dto.UpdateCartProductOptionCommand
import com.petqua.domain.cart.CartProductQuantity
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
            deliveryMethod = DeliveryMethod.from(deliveryMethod)
        )
    }
}

data class UpdateCartProductOptionRequest(
    val quantity: Int,
    val isMale: Boolean,
    val deliveryMethod: String,
) {

    fun toCommand(memberId: Long, cartProductId: Long): UpdateCartProductOptionCommand {
        return UpdateCartProductOptionCommand(
            memberId = memberId,
            cartProductId = cartProductId,
            quantity = CartProductQuantity(quantity),
            isMale = isMale,
            deliveryMethod = DeliveryMethod.from(deliveryMethod)
        )
    }
}

data class DeleteCartProductRequest(
    val cartProductId: Long
) {

    fun toCommand(memberId: Long): DeleteCartProductCommand {
        return DeleteCartProductCommand(
            memberId = memberId,
            cartProductId = cartProductId,
        )
    }
}
