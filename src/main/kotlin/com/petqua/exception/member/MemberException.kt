package com.petqua.exception.member

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class MemberException(
    private val exceptionType: MemberExceptionType,
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return exceptionType
    }
}
