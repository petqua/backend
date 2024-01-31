package com.petqua.exception.cart

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class CartProductException(
    private val exceptionType: CartProductExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
