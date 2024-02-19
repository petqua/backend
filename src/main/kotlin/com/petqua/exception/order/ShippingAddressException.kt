package com.petqua.exception.order

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class ShippingAddressException(
    private val exceptionType: ShippingAddressExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
