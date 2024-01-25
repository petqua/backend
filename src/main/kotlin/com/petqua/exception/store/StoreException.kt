package com.petqua.exception.store

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class StoreException(
    private val exceptionType: StoreExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
