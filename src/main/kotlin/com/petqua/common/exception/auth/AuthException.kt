package com.petqua.common.exception.auth

import com.petqua.common.exception.BaseException
import com.petqua.common.exception.BaseExceptionType

class AuthException(
    private val authExceptionType: AuthExceptionType
) : BaseException() {

    override fun exceptionType(): BaseExceptionType {
        return authExceptionType
    }
}
