package com.petqua.exception.product

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class ProductException(
        private val exceptionType: ProductExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
