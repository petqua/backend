package com.petqua.exception.wish

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class WishException(
    private val exceptionType: WishExceptionType
) : BaseException() {
    override fun exceptionType(): BaseExceptionType {
        TODO("Not yet implemented")
    }
}
