package com.petqua.application.cart.dto

import com.petqua.domain.cart.CartProduct
import com.petqua.domain.cart.DeliveryMethod

data class SaveCartProductCommand(
    val memberId: Long,
    val productId: Long,
    val quantity: Int,
    val isMale: Boolean,
    val deliveryMethod: DeliveryMethod,
) {
    fun toCartProduct(): CartProduct {
        return CartProduct(
            memberId = memberId,
            productId = productId,
            quantity = quantity,
            isMale = isMale,
            deliveryMethod = deliveryMethod,
        )
    }
}