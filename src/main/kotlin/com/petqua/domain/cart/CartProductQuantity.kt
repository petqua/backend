package com.petqua.domain.cart

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_OVER_MAXIMUM
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_UNDER_MINIMUM
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val MIN_QUANTITY = 1
private const val MAX_QUANTITY = 99

@Embeddable
class CartProductQuantity(
    @Column(nullable = false)
    val value: Int = 1,
) {

    init {
        throwExceptionWhen(value < MIN_QUANTITY) { CartProductException(PRODUCT_QUANTITY_UNDER_MINIMUM) }
        throwExceptionWhen(value > MAX_QUANTITY) { CartProductException(PRODUCT_QUANTITY_OVER_MAXIMUM) }
    }
}
