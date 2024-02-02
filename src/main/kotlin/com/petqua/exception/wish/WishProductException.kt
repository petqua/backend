package com.petqua.exception.wish

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class WishProductException(
    private val exceptionType: WishProductExceptionType
) : BaseException() {
    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
