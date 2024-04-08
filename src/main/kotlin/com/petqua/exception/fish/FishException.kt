package com.petqua.exception.fish

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class FishException(
    private val exceptionType: FishExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
