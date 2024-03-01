package com.petqua.exception.payment

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class FailPaymentException(
    private val exceptionType: FailPaymentExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
