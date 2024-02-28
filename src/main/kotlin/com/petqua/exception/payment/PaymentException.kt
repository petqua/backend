package com.petqua.exception.payment

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class PaymentException(
    private val exceptionType: PaymentExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
