package com.petqua.exception.order

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class OrderException(
    private val exceptionType: OrderExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
