package com.petqua.exception.order

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class OrderPaymentException(
    private val exceptionType: OrderPaymentExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
