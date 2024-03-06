package com.petqua.exception.product

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class ProductSnapshotException(
    private val exceptionType: ProductSnapshotExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
