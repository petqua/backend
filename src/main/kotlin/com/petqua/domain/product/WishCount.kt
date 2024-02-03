package com.petqua.domain.product

import com.petqua.common.util.throwExceptionWhen
import com.petqua.exception.product.ProductException
import com.petqua.exception.product.ProductExceptionType.WISH_COUNT_UNDER_MINIMUM
import jakarta.persistence.Embeddable

private const val MIN_QUANTITY = 0

@Embeddable
class WishCount(
    val value: Int = 0,
) {

    init {
        throwExceptionWhen(value < MIN_QUANTITY) { ProductException(WISH_COUNT_UNDER_MINIMUM) }
    }

    fun plus(): WishCount {
        return WishCount(value + 1)
    }

    fun minus(): WishCount {
        return WishCount(value - 1)
    }

    override fun equals(other: Any?): Boolean {
        return other is WishCount && value == other.value
    }

    override fun hashCode(): Int {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}