package com.petqua.test.fixture

import com.petqua.domain.cart.CartProduct
import com.petqua.domain.cart.CartProductQuantity
import com.petqua.domain.cart.DeliveryMethod

fun cartProduct(
    id: Long = 1L,
    memberId: Long = 1L,
    productId: Long = 1L,
    quantity: Int = 1,
    isMale: Boolean = true,
    deliveryMethod: DeliveryMethod = DeliveryMethod.COMMON,
): CartProduct {
    return CartProduct(
        id = id,
        memberId = memberId,
        productId = productId,
        quantity = CartProductQuantity(quantity),
        isMale = isMale,
        deliveryMethod = deliveryMethod,
    )
}
