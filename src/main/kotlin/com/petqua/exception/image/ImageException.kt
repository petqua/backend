package com.petqua.exception.image

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class ImageException(
    private val exceptionType: ImageExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
