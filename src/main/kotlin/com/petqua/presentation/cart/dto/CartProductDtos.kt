package com.petqua.presentation.cart.dto

import com.petqua.application.cart.dto.DeleteCartProductsCommand
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

data class DeleteCartProductsRequest(
    val cartProductIds: List<Long>,
) {

    fun toCommand(memberId: Long): DeleteCartProductsCommand {
        return DeleteCartProductsCommand(
            memberId = memberId,
            cartProductIds = cartProductIds,
        )
    }
}
