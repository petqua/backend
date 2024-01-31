package com.petqua.domain.cart

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.cart.CartProductException
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_OVER_MAXIMUM
import com.petqua.exception.cart.CartProductExceptionType.PRODUCT_QUANTITY_UNDER_MINIMUM
import jakarta.persistence.Embeddable

private const val MIN_QUANTITY = 1
private const val MAX_QUANTITY = 99

@Embeddable
class CartProductQuantity(
    val value: Int = 1,
) {

    init {
        throwExceptionWhen(value < MIN_QUANTITY) { CartProductException(PRODUCT_QUANTITY_UNDER_MINIMUM) }
        throwExceptionWhen(value > MAX_QUANTITY) { CartProductException(PRODUCT_QUANTITY_OVER_MAXIMUM) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartProductQuantity

        return value == other.value
    }

    override fun hashCode(): Int {
        return value
    }
}
